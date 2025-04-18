//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[buildQuery](build-query.md)

# buildQuery

[jvm]\
fun [buildQuery](build-query.md)(init: [SQLBuilder](-s-q-l-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-pair/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?&gt;&gt;

Build a SQL query string using the given [init](build-query.md) block.

Returns the SQL string and the parameters.

#### See also

| | |
|---|---|
| [buildSql](build-sql.md) | for a version that returns only the SQL string. |
