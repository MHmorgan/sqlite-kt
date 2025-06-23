package dev.hirth.sqlite

/**
 * [SQLReader] should be implemented by objects to allow using operator
 * functions to read from a database.
 *
 * ```kotlin
 * class Repository {
 *     val users = object : SQLReader<String, User> { /* ... */ }
 * }
 *
 * val repo = Repository()
 *
 * // Read all users
 * repo.users()
 *
 * // Read a specific user
 * repo.users["alice"]
 *
 * if ("alice" in repo.users) {
 *     // Do something with the user
 * }
 * ```
 */
interface SQLReader<I, T> {
    /**
     * Read all values from the database.
     */
    operator fun invoke(): List<T>

    /**
     * Read a specific value from the database.
     */
    operator fun get(identifier: I): T?

    /**
     * Check if a specific value exists in the database.
     */
    operator fun contains(identifier: I): Boolean = get(identifier) != null
}