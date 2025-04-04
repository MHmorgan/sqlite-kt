import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

open class SQLiteException(msg: String) : IllegalStateException(msg)

class SQLite(private val config: Config) : AutoCloseable {
    private val conn: Connection

    private var nestCnt: Int = 0

    init {
        conn = DriverManager.getConnection(config.url)!!
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
        conn.close()
    }

    data class Config(
        val url: String,
        val name: String = url,
    )
}

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
