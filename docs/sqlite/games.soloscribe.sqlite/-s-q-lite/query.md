//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)/[query](query.md)

# query

[jvm]\
fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md), rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](query.md)&gt;): [T](query.md)

Execute the sql query from [sql](query.md) with [params](query.md) and extract the result with [rse](query.md).

If the query contains multiple statements, only the last one will return a result. The rest will be executed and discarded. If this is not the desired behaviour, check out multiQuery.

[jvm]\
fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](query.md)&gt;): [T](query.md)

Execute the sql query from src and extract the result with [rse](query.md).

If the query contains multiple statements, only the last one will return a result. The rest will be executed and discarded. If this is not the desired behaviour, check out multiQuery.

[jvm]\
fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md), rm: [RowMapper](../-row-mapper/index.md)&lt;[T](query.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](query.md)&gt;&gt;

Execute the sql query from [sql](query.md) with [params](query.md) and map the result to a list with [rm](query.md).

If the query contains multiple statements, only the last one will return a result. The rest will be executed and discarded. If this is not the desired behaviour, check out multiQuery.

[jvm]\
fun &lt;[T](query.md)&gt; [query](query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), rm: [RowMapper](../-row-mapper/index.md)&lt;[T](query.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](query.md)&gt;&gt;

Execute the sql query from [sql](query.md) and map the result to a list with [rm](query.md).

If the query contains multiple statements, only the last one will return a result. The rest will be executed and discarded. If this is not the desired behaviour, check out multiQuery.