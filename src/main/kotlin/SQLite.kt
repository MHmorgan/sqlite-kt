import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

typealias SQLParams = Map<String, Any?>

open class SQLiteException(msg: String) : IllegalStateException(msg)

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

    /**
     * The lowest common denominator for every kind of sql execution.
     * It ensures thread safety and helps with error handling.
     */
    private fun <T> exec(sql: String, func: (PreparedStatement) -> T): T {
        val user = Thread.currentThread()
        if (user != owner) {
            val msg = "${config.name}: SQLite is not thread-safe: Owner=$owner User=$user"
            throw SQLiteException(msg)
        }

        return try {
            conn.prepareStatement(sql).use { ps ->
                func(ps)
            }
        } catch (e: SQLException) {
            val msg = "${config.name}: SQL error: ${e.message}\n${sql.trimIndent()}"
            throw SQLiteException(msg)
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
        val stmts = sql.parse()

        if (stmts.isEmpty() && sql.isBlank())
            throw SQLiteException("${config.name}: Empty query")
        else if (stmts.isEmpty() && sql.isNotBlank())
            throw SQLiteException("${config.name}: Invalid query: $sql")

        // Execute every statement, but the last one.
        for (i in 0..stmts.size - 2) {
            val (sql, params) = stmts[i].resolve(params)
            exec(sql) { ps ->
                setParameters(ps, params)
                ps.execute()
            }
        }

        val (sql, params) = stmts.last().resolve(params)
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


    /**
     * Execute all the sql statements from [src] with [params]. This should be
     * used when there are multiple queries in [src], which requires multiple
     * result sets to be extracted.
     *
     * The results are extracted using calling the extension [block] on a
     * [MultiResult] which allows the caller to control the extraction of
     * multiple result sets.
     */
    fun <T> multiQuery(sql: String, params: SQLParams, block: MultiResult.() -> T): T {
        val stmts = sql.parse()

        if (stmts.isEmpty() && sql.isBlank())
            throw SQLiteException("${config.name}: Empty query")
        else if (stmts.isEmpty() && sql.isNotBlank())
            throw SQLiteException("${config.name}: Invalid query: $sql")

        val sql = if (params.isEmpty())
            stmts.joinToString("") { it.resolveSQL() }
        else
            stmts.joinToString("") { it.resolveSQL(params) }

        val params = buildList {
            if (params.isEmpty())
                return@buildList
            for (stmt in stmts)
                addAll(stmt.resolveParams(params))
        }.toTypedArray()

        return exec(sql) { ps ->
            setParameters(ps, params)
            val hasResults = ps.execute()
            val multiResult = MultiResult(hasResults, ps)
            val res = multiResult.block()
            check(!multiResult.hasResults()) {
                "${config.name}: Multi query block did not consume all results"
            }
            res
        }
    }

    /**
     * Execute all the sql statements from [sql]. This should be
     * used when there are multiple queries in [sql], which requires multiple
     * result sets to be extracted.
     *
     * The results are extracted using calling the extension [block] on a
     * [MultiResult] which allows the caller to control the extraction of
     * multiple result sets.
     */
    fun <T> multiQuery(sql: String, block: MultiResult.() -> T) =
        multiQuery(sql, emptyMap(), block)

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
            if (p is SQLValue<*>)
                ps.setObject(i + 1, p.sqlValue())
            else
                ps.setObject(i + 1, p)
        }
    }

    data class Config(
        val url: String,
        val name: String,
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

// -------------------------------------------------------------------------
//
// MultiResult
//
// -------------------------------------------------------------------------

/**
 * A helper class for extracting multiple results from a query.
 *
 * It provides methods (a tiny DDL) for extracting result sets by using
 * [ResultSetExtractor]s and [RowMapper]s.
 */
class MultiResult(
    private var moreResults: Boolean,
    private var ps: PreparedStatement,
) {
    private var updateCount = ps.updateCount

    private fun getMoreResults() {
        moreResults = ps.moreResults
        // Calling `ps.updateCount` should only be done once for each
        // result set, so we cache it here.
        updateCount = ps.updateCount
    }

    /**
     * Return `true` if there are more results which hasn't been extracted
     * yet, and `false` otherwise.
     */
    internal fun hasResults(): Boolean {
        // When `hasResults == false` and `updateCount > -1` it means that
        // the prepared statement is currently pointing at a DML statement
        // (INSERT, UPDATE, DELETE) which didn't return a result set.
        // We continue until we reach a result set or the end of the results.
        while (!moreResults && updateCount != -1)
            getMoreResults()
        return moreResults
    }

    /**
     * Get the next [ResultSet] from the query, and extract it with the
     * given result set extractor.
     *
     * @throws NoSuchElementException if there are no more results.
     */
    fun <T> next(rse: ResultSetExtractor<T>): T {
        // When `hasResults == false` and `updateCount > -1` it means that
        // the prepared statement is currently pointing at a DML statement
        // (INSERT, UPDATE, DELETE) which didn't return a result set.
        // We continue until we reach a result set or the end of the results.
        while (!moreResults && updateCount != -1)
            getMoreResults()

        if (!moreResults)
            throw NoSuchElementException()

        val res = rse.extract(ps.resultSet)
        // This _must_ be done after extracting the result set, because it
        // has the side effect of closing the current result set.
        getMoreResults()
        return res
    }

    /**
     * Get the next [ResultSet] from the query, and map it with the given
     * row mapper.
     *
     * Throws [NoSuchElementException] if there are no more results.
     */
    fun <T> next(rm: RowMapper<T>): List<Result<T>> = next(rm::mapAll)

    /**
     * Get all [ResultSet]s from the query, extracting them with the given
     * result set extractor. Returns a list of the extracted results.
     */
    fun <T> all(rse: ResultSetExtractor<T>): List<T> = buildList {
        while (moreResults || updateCount != -1) {
            if (moreResults) {
                val res = rse.extract(ps.resultSet)
                add(res)
            }
            getMoreResults()
        }
    }

    /**
     * Get all [ResultSet]s from the query, mapping them with the given
     * row mapper. Returns a flattened list of the mapped results.
     */
    fun <T> all(rm: RowMapper<T>): List<Result<T>> {
        val res = buildList {
            while (moreResults || updateCount != -1) {
                if (moreResults) {
                    val res = rm.mapAll(ps.resultSet)
                    add(res)
                }
                getMoreResults()
            }
        }
        return res.flatten()
    }
}
