package dev.hirth.sqlite

import org.slf4j.Logger
import java.sql.ResultSet

/**
 * Base class for resources which are backed by a database table.
 *
 * @param I The type of the identifier for the resource.
 * @param T The type of the resource.
 * @property name The name of the resource.
 * @property log The logger to use for logging.
 * @property db The database to use for accessing the resource.
 *
 * @see SQLite
 */
abstract class SQLResource<I, T>(
    val name: String,
    val log: Logger,
    val db: SQLite
) : SQLReader<I, T> {
    /**
     * The DQL statement to select all items of the resource.
     * The statement should not contain any parameters.
     *
     * Example: `SELECT * FROM users`
     *
     * @see selectByIdSql
     * @see get
     */
    abstract val selectAllSql: String

    /**
     * The DQL statement to select a single item of the resource by its identifier.
     * The statement should contain a single parameter for the id column,
     * and reference the `:id` input parameter.
     *
     * Example: `SELECT * FROM users WHERE id = :id`
     *
     * @see selectAllSql
     * @see invoke
     */
    abstract val selectByIdSql: String

    /**
     * The DML statement to insert a single item of the resource.
     * The statement must match the parameters returned by [params].
     *
     * Example: `INSERT OR REPLACE INTO users (name, age) VALUES (:name, :age)`
     *
     * @see params
     * @see insert
     */
    abstract val insertSql: String

    /**
     * The DML statement to delete a single item of the resource by its identifier.
     * The statement must contain a single parameter for the id column,
     * and reference the `:id` input parameter.
     *
     * Example: `DELETE FROM users WHERE id = :id`
     *
     * @see delete
     */
    abstract val deleteSql: String

    /**
     * Returns the parameters for the [insertSql] statement.
     *
     * @param item The item to insert.
     * @return The parameters for the [insertSql] statement.
     * @see insertSql
     * @see insert
     */
    abstract fun params(item: T): Map<String, Any?>

    /**
     * Maps a row in a [ResultSet] to an instance of [T].
     *
     * @param rs The [ResultSet] to map.
     * @param lineNum The line number of the row in the [ResultSet].
     * @return The mapped instance of [T].
     */
    abstract fun rowMapper(rs: ResultSet, lineNum: Int): T

    /**
     * Get all items of the resource.
     *
     * @see selectAllSql
     */
    override fun invoke(): List<T> {
        log.trace("Getting all {} items", name)
        return db.query(selectAllSql, ::rowMapper).getOrThrow()
    }

    /**
     * Get all items of the resource, applying the given [block]
     * to modify the [selectAllSql] query before executing it.
     *
     * @see selectAllSql
     */
    operator fun invoke(block: SQLBuilder.() -> Unit = {}): List<T> {
        log.trace("Getting all {} items (with modified sql)", name)
        val (sql, params) = buildQuery {
            +selectAllSql
            block()
        }
        return db.query(sql, params, ::rowMapper).getOrThrow()
    }

    /**
     * Get a single item of the resource by its identifier.
     * Returns `null` if the item does not exist.
     *
     * @see selectByIdSql
     */
    override fun get(identifier: I): T? {
        log.trace("Get {} item with identifier: {}", name, identifier)
        val params = mapOf("id" to identifier)
        return db.query(selectByIdSql, params, ::rowMapper)
            .getSingleOrNull()
    }

    /**
     * Check if a single item of the resource exists by its identifier.
     */
    override fun contains(identifier: I): Boolean = get(identifier) != null

    /**
     * Insert a single item of the resource.
     * Returns the number of affected rows.
     *
     * @see insertSql
     * @see params
     */
    fun insert(item: T): Int {
        log.trace("Storing {} item", name)
        return db.execute(insertSql, params(item))
    }

    /**
     * Insert a single item of the resource.
     *
     * @see insertSql
     * @see params
     */
    operator fun plusAssign(item: T) {
        insert(item)
    }

    /**
     * Delete a single item of the resource by its identifier.
     * Returns the number of affected rows.
     *
     * @see deleteSql
     */
    fun delete(identifier: I): Int {
        log.trace("Deleting {} item with identifier: {}", name, identifier)
        val params = mapOf("id" to identifier)
        return db.execute(deleteSql, params)
    }

    /**
     * Delete a single item of the resource by its identifier.
     */
    operator fun minusAssign(identifier: I) {
        delete(identifier)
    }
}