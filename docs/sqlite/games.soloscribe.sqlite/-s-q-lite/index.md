//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)

# SQLite

[jvm]\
class [SQLite](index.md)(config: [SQLite.Config](-config/index.md)) : [AutoCloseable](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html)

## Constructors

| | |
|---|---|
| [SQLite](-s-q-lite.md) | [jvm]<br>constructor(config: [SQLite.Config](-config/index.md)) |

## Types

| Name | Summary |
|---|---|
| [Config](-config/index.md) | [jvm]<br>data class [Config](-config/index.md)(val url: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val foreignKeys: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)? = null) |
| [SchemaRow](-schema-row/index.md) | [jvm]<br>data class [SchemaRow](-schema-row/index.md)(val type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val tblName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val rootpage: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)?) |

## Functions

| Name | Summary |
|---|---|
| [batchUpdate](batch-update.md) | [jvm]<br>fun [batchUpdate](batch-update.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), batches: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[SQLParams](../-s-q-l-params/index.md)&gt;): [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int-array/index.html)<br>Execute the sql statement from [sql](batch-update.md) with the [batches](batch-update.md) of named parameters and return an array with the number of affected rows for each batch. |
| [checkForeignKeys](check-foreign-keys.md) | [jvm]<br>fun [checkForeignKeys](check-foreign-keys.md)() |
| [close](close.md) | [jvm]<br>open override fun [close](close.md)()<br>Close the sqlite connection. This should not be called directly, but rather used with the use function. |
| [execute](execute.md) | [jvm]<br>fun [execute](execute.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Execute the [sql](execute.md) statements.<br>[jvm]<br>fun [execute](execute.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Execute the [sql](execute.md) statements with named [params](execute.md). |
| [query](query.md) | [jvm]<br>fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](query.md)&gt;): [T](query.md)<br>Execute the sql query from src and extract the result with [rse](query.md).<br>[jvm]<br>fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), rm: [RowMapper](../-row-mapper/index.md)&lt;[T](query.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](query.md)&gt;&gt;<br>Execute the sql query from [sql](query.md) and map the result to a list with [rm](query.md).<br>[jvm]<br>fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md), rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](query.md)&gt;): [T](query.md)<br>Execute the sql query from [sql](query.md) with [params](query.md) and extract the result with [rse](query.md).<br>[jvm]<br>fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md), rm: [RowMapper](../-row-mapper/index.md)&lt;[T](query.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](query.md)&gt;&gt;<br>Execute the sql query from [sql](query.md) with [params](query.md) and map the result to a list with [rm](query.md). |
| [schema](schema.md) | [jvm]<br>fun [schema](schema.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[SQLite.SchemaRow](-schema-row/index.md)&gt;<br>Get the schema of the database, from the sqlite_schema table. |
| [transaction](transaction.md) | [jvm]<br>fun &lt;[T](transaction.md)&gt; [transaction](transaction.md)(func: () -&gt; [T](transaction.md)): [T](transaction.md)<br>Run the given [func](transaction.md) inside a transaction, committing if it succeeds and rolling back if it fails. |
