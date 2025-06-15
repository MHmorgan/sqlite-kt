package dev.hirth.sqlite

/**
 * [SQLValue] should be implemented by classes which can be
 * converted to a type that can be understood by the SQL driver.
 *
 * Any class implementing this interface can be supplied as a
 * parameter to [SQLite] statements. The value will be extracted
 * from the object and used in the query. This of course means
 * that the value returned by [sqlValue] must be a valid SQL
 * value, e.g. the database driver must know how to handle it.
 */
interface SQLValue<T> {
    fun sqlValue(): T
}
