@file:Suppress("NAME_SHADOWING")

package games.soloscribe.sqlite

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import java.util.UUID

typealias SQLParams = Map<String, Any?>

open class SQLiteException(msg: String, cause: Throwable? = null) :
    SQLException(msg, cause)

class SQLite(private val config: Config) : AutoCloseable {
    private val conn: Connection = DriverManager.getConnection(config.url)!!

    /**
     * The thread that owns the librarian is the only one allowed to use it.
     * This is to ensure that the librarian is not used concurrently, which
     * it's not designed for.
     * Concurrent use of a data source on the other hand is fine.
     */
    private val owner: Thread = Thread.currentThread()

    private var nestCnt: Int = 0

    init {
        config.foreignKeys?.let { fk ->
            if (fk)
                executeRaw("PRAGMA foreign_keys=ON")
            else
                executeRaw("PRAGMA foreign_keys=OFF")
        }
    }

    /**
     * Run the given [func] inside a transaction, committing if it succeeds
     * and rolling back if it fails.
     */
    fun <T> transaction(func: () -> T): T {
        try {
            if (nestCnt++ == 0) conn.autoCommit = false
            val res = func()
            // This is the key for correctly handling nested transactions:
            // only commit the outermost transaction.
            if (nestCnt == 1) conn.commit()
            return res
        } catch (e: Throwable) {
            try {
                // Only rollback the outermost transaction. Otherwise, we loose
                // the possibility of recovering.
                if (nestCnt == 1) conn.rollback()
            } catch (e: SQLException) {
                e.addSuppressed(e)
            }
            throw e
        } finally {
            conn.autoCommit = --nestCnt == 0
        }
    }

    /**
     * Close the sqlite connection. This should not be called directly,
     * but rather used with the [use] function.
     *
     * After closing, [SQLite] should not be used again.
     */
    override fun close() {
        if (conn.isClosed) return
        conn.close()
    }

    /**
     * Get the schema of the database, from the sqlite_schema table.
     */
    fun schema(): List<SchemaRow> {
        val sql = "SELECT * FROM sqlite_schema"
        return query(sql) { rs ->
            val res = mutableListOf<SchemaRow>()
            while (rs.next()) {
                val row = SchemaRow(
                    type = rs.getString("type")!!,
                    name = rs.getString("name")!!,
                    tblName = rs.getString("tbl_name")!!,
                    rootpage = rs.getInt("rootpage"),
                    sql = rs.getString("sql"),
                )
                res.add(row)
            }
            res
        }
    }

    fun checkForeignKeys() {
        executeRaw("PRAGMA foreign_key_check")
    }

    /**
     * The lowest common denominator for every kind of sql execution.
     * It ensures thread safety and helps with error handling.
     */
    private fun <T> exec(sql: String, func: (PreparedStatement) -> T): T {
        val user = Thread.currentThread()
        if (user != owner)
            err("SQLite is not thread-safe: Owner=$owner User=$user")

        return try {
            conn.prepareStatement(sql).use { ps ->
                func(ps)
            }
        } catch (e: SQLException) {
            err("SQLite error for statement:\n${sql.trimIndent()}", e)
        }
    }

    // -------------------------------------------------------------------------
    //
    // Query
    //
    // -------------------------------------------------------------------------

    /**
     * Execute the sql query from [sql] with [params] and extract the result
     * with [rse].
     *
     * If the query contains multiple statements, only the last one will
     * return a result. The rest will be executed and discarded.
     * If this is not the desired behaviour, check out [multiQuery].
     */
    fun <T> query(sql: String, params: SQLParams, rse: ResultSetExtractor<T>): T {
        val statements = sql.parse()

        if (statements.isEmpty() && sql.isBlank())
            err("Empty query")
        else if (statements.isEmpty() && sql.isNotBlank())
            err("Invalid query: $sql")

        // Execute every statement, but the last one.
        for (i in 0..statements.size - 2) {
            val (sql, params) = statements[i].resolve(params)
            exec(sql) { ps ->
                setParameters(ps, params)
                ps.execute()
            }
        }

        val (sql, params) = statements.last().resolve(params)
        return exec(sql) { ps ->
            setParameters(ps, params)
            val rs = ps.executeQuery()
            rse.extract(rs)
        }
    }

    /**
     * Execute the sql query from [src] and extract the result with [rse].
     *
     * If the query contains multiple statements, only the last one will
     * return a result. The rest will be executed and discarded.
     * If this is not the desired behaviour, check out [multiQuery].
     */
    fun <T> query(sql: String, rse: ResultSetExtractor<T>) =
        query(sql, emptyMap(), rse)

    /**
     * Execute the sql query from [sql] with [params] and map the result to a
     * list with [rm].
     *
     * If the query contains multiple statements, only the last one will
     * return a result. The rest will be executed and discarded.
     * If this is not the desired behaviour, check out [multiQuery].
     */
    fun <T> query(sql: String, params: SQLParams, rm: RowMapper<T>) =
        query(sql, params, rm::mapAll)

    /**
     * Execute the sql query from [sql] and map the result to a list with [rm].
     *
     * If the query contains multiple statements, only the last one will
     * return a result. The rest will be executed and discarded.
     * If this is not the desired behaviour, check out [multiQuery].
     */
    fun <T> query(sql: String, rm: RowMapper<T>) =
        query(sql, emptyMap(), rm::mapAll)

    // -------------------------------------------------------------------------
    //
    // Batch Update
    //
    // -------------------------------------------------------------------------

    /**
     * Execute the sql statement from [sql] with the [batches] of named
     * parameters and return an array with the number of affected rows for each
     * batch.
     */
    fun batchUpdate(sql: String, batches: List<SQLParams>): IntArray {
        // Missing batch update support could probably be emulated, if needed.
        if (!conn.metaData.supportsBatchUpdates())
            err("Batch updates are not supported")

        val statement = sql.parse().singleOrNull()
            ?: err("Batch SQL must contain exactly one statement:\n$sql")

        val sql = if (batches.isEmpty()) {
            statement.resolveSQL()
        } else {
            // If the batches are of different sizes we have a problem,
            // but that's the caller's responsibility.
            statement.resolveSQL(batches[0])
        }

        return exec(sql) { ps ->
            for (batch in batches) {
                val params = statement.resolveParams(batch)
                setParameters(ps, params)
                ps.addBatch()
            }
            ps.executeBatch()
        }
    }

    // -------------------------------------------------------------------------
    //
    // Execute
    //
    // -------------------------------------------------------------------------

    /**
     * Execute the [sql] statements with named [params].
     *
     * If the [sql] contains multiple statements, they are all executed within
     * the same transaction.
     */
    fun execute(sql: String, params: SQLParams): Int {
        var sum: Int? = null
        val statements = sql.parse()

        transaction {
            for (statement in statements) {
                val (sql, args) = statement.resolve(params)
                exec(sql) { ps ->
                    setParameters(ps, args)
                    ps.execute()
                    val c = ps.updateCount
                    if (c != -1) sum = (sum ?: 0) + c
                }
            }
        }

        return sum ?: -1
    }

    /**
     * Execute the [sql] statements.
     */
    fun execute(sql: String) = execute(sql, emptyMap())

    private fun executeRaw(sql: String): Int {
        return exec(sql) { ps ->
            ps.execute()
            ps.updateCount
        }
    }

    // -------------------------------------------------------------------------
    //
    // Helpers
    //
    // -------------------------------------------------------------------------

    /**
     * Set the parameters of a prepared statement, properly handling [SQLValue]s.
     */
    private fun setParameters(ps: PreparedStatement, params: Array<out Any?>) {
        if (params.isEmpty()) return

        for ((i, p) in params.withIndex()) {
            val obj = when (p) {
                is Temporal,
                is TemporalAmount,
                is UUID,
                    -> p.toString()

                is Enum<*> -> p.name
                is SQLValue<*> -> p.sqlValue()
                else -> p
            }

            ps.setObject(i + 1, obj)
        }
    }

    /**
     * Throw an [SQLiteException] with the given message and a prefix.
     */
    private fun err(msg: String, cause: Throwable? = null): Nothing =
        throw SQLiteException("${config.name}: $msg", cause)

    data class Config(
        val url: String,
        val name: String,
        val foreignKeys: Boolean? = null,
    )

    data class SchemaRow(
        val type: String,
        val name: String,
        val tblName: String,
        val rootpage: Int,
        val sql: String?,
    ) {
        val isTable: Boolean
            get() = type == "table"

        val isIndex: Boolean
            get() = type == "index"

        val isTrigger: Boolean
            get() = type == "trigger"

        val isView: Boolean
            get() = type == "view"
    }
}

// -------------------------------------------------------------------------
//
// RowMapper & ResultSetExtractor
//
// -------------------------------------------------------------------------

/**
 * The interface used by [SQLite] for mapping rows of a [ResultSet]
 */
fun interface RowMapper<T> {
    /**
     * Map a single row of data from a [ResultSet] to an object.
     * This method must not call [ResultSet.next] or [ResultSet.close].
     */
    fun map(rs: ResultSet, rowNum: Int): T

    /**
     * Map all rows of data from a [ResultSet] to a list of objects.
     *
     * The results of [map] are wrapped in a [Result] to allow for
     * cleaner handling of errors, since the first encountered error won't
     * stop the entire mapping process.
     */
    fun mapAll(rs: ResultSet): List<Result<T>> {
        val results = mutableListOf<Result<T>>()
        while (rs.next()) {
            val res = runCatching { map(rs, rs.row) }
            results.add(res)
        }
        return results
    }
}

/**
 * The interface used by [SQLite] for extracting a result
 * from an entire [ResultSet].
 */
fun interface ResultSetExtractor<T> {
    /**
     * Extract data from a [ResultSet].
     */
    fun extract(rs: ResultSet): T
}
