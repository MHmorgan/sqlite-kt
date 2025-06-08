package games.soloscribe.sqlite

import org.intellij.lang.annotations.Language

@DslMarker
annotation class SQLDsl

/**
 * A sql query builder. This provides a convenient way of building sql
 * queries with named parameters in a flexible way.
 */
@SQLDsl
class SQLBuilder {
    /**
     * The main query string.
     */
    private val query = StringBuilder()

    /**
     * Common Table Expressions (CTEs) to be appended before the main query.
     */
    private val ctes = mutableListOf<Pair<String, String>>()

    private val params = mutableMapOf<String, Any?>()

    operator fun @receiver:Language("SQLite") String.unaryPlus(): StringBuilder =
        query.append(this).append(" ")

    fun params() = params.toMap()

    fun sql(): String {
        val sql = buildString {
            for ((i, pair) in ctes.withIndex()) {
                val (name, cte) = pair
                if (i == 0) append("WITH ")
                append("$name AS (")
                append(cte)
                if (i < ctes.size - 1) append("),\n")
                else append(")\n")
            }
            append(query)
        }
        return sql.trimIndent()
    }

    /**
     * Set the query parameter with [name] to the given [value].
     */
    fun setParam(name: String, value: Any?) {
        params[name] = value
    }

    /**
     * Add a common table expression (CTE) with the given [name] and [block].
     */
    fun withCte(name: String, block: SQLBuilder.() -> Unit) {
        val sb = SQLBuilder()
        sb.block()
        val cte = sb.query.toString()
        ctes.add(name to cte)
        params.putAll(sb.params)
    }
}

/**
 * Build a SQL query string using the given [init] block.
 *
 * Returns only the SQL string.
 *
 * @see buildQuery for a version that returns the SQL string and parameters.
 */
fun buildSql(init: SQLBuilder.() -> Unit): String {
    val sb = SQLBuilder()
    sb.init()
    return sb.sql()
}

/**
 * Build a SQL query string using the given [init] block.
 *
 * Returns the SQL string and the parameters.
 *
 * @see buildSql for a version that returns only the SQL string.
 */
fun buildQuery(init: SQLBuilder.() -> Unit): Pair<String, Map<String, Any?>> {
    val sb = SQLBuilder()
    sb.init()
    return sb.sql() to sb.params()
}
