package games.soloscribe.sqlite

import java.util.Objects

/**
 * [SQLCollection] is a base class for collections used as parameters in SQL
 * queries.
 *
 * [SQLCollection]s are purely semantic classes which convey explicit
 * information about how the collection parameters should be handled,
 * which provide flexibility and makes it easy to build complex queries.
 * The explicit solution is, as always, superior to any implicit solution
 * by eliminating a range of edge cases and limitations.
 *
 * All sql collections are recursively expanded.
 *
 * Example of a complicated query:
 *
 * ```kotlin
 *  SqlList("One", SqlPair(1, 2), List(SqlPair(3, 4)), SqlList(5, 6))
 * ```
 *
 * Which expands to:
 *
 * ```kotlin
 *  val resolvedSql = "?,(?,?),?,?,?"
 *  val resolvedParams = arrayOf("One", 1, 2, SqlPair(3, 4), 5, 6)
 * ```
 *
 * What happens here is:
 *
 * 1. `"One"` is expanded to `?`
 * 2. `SqlPair(1, 2)` is expanded to `(?,?)`
 * 3. `List(SqlPair(3, 4))` is expanded to `?`
 *    (because normal lists aren't recursively expanded)
 * 4. `SqlList(5, 6)` is expanded to `?,?`
 */
sealed class SQLCollection(protected val items: List<Any?>) {

    val size: Int
        get() = items.size

    /**
     * Resolve the SQL string representation of the parameters for this
     * collection (a string full of `?`).
     */
    internal abstract fun resolveSql(): String

    /**
     * Resolve the flattened list of parameters for this collection.
     */
    internal abstract fun resolveParams(): List<Any?>

    override fun toString(): String {
        val name = this::class.simpleName ?: "SqlCollection"
        return "$name(${items.joinToString()})"
    }
}

/**
 * [SQLList] is a collection of items which should be expanded to a
 * comma-separated list in a SQL query.
 *
 * Example: `SqlList(1, 2, 3)` expands to `?,?,?`
 *
 * The difference between using [SQLList] and [List] is that [SQLList] is
 * recursively expanded, meaning that if [items] contains other
 * [SQLCollection]s they will be expanded as well.
 */
class SQLList(items: List<Any?>) : SQLCollection(items) {
    constructor(vararg items: Any?) : this(items.toList())
    constructor(items: Collection<Any?>) : this(items.toList())

    override fun resolveSql(): String {
        return items.joinToString(",") {
            when (it) {
                is SQLCollection -> it.resolveSql()
                is List<*> -> it.joinToString { "?" }
                else -> "?"
            }
        }
    }

    override fun resolveParams(): List<Any?> {
        return items.flatMap {
            when (it) {
                is SQLCollection -> it.resolveParams()
                is List<*> -> it
                else -> listOf(it)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SQLList) return false
        return items == other.items
    }

    override fun hashCode() = Objects.hash(this.javaClass, items)
}

/**
 * [SQLPair] is a collection of items which should be expanded to a
 * parenthesis enclosed pair in a SQL query.
 *
 * Example: `SqlPair(1, 2)` expands to `(?,?)`
 */
class SQLPair(first: Any?, second: Any?) : SQLCollection(listOf(first, second)) {
    constructor(pair: Pair<Any?, Any?>) : this(pair.first, pair.second)

    override fun resolveSql() = "(?,?)"

    override fun resolveParams() = items

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SQLPair) return false
        return items == other.items
    }

    override fun hashCode() = Objects.hash(this.javaClass, items)
}
