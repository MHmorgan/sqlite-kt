//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[buildSql](build-sql.md)

# buildSql

[jvm]\
fun [buildSql](build-sql.md)(init: [SQLBuilder](-s-q-l-builder/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)

Build a SQL query string using the given [init](build-sql.md) block.

Returns only the SQL string.

#### See also

| | |
|---|---|
| [buildQuery](build-query.md) | for a version that returns the SQL string and parameters. |
