//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[MultiResult](index.md)/[next](next.md)

# next

[jvm]\
fun &lt;[T](next.md)&gt; [next](next.md)(rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](next.md)&gt;): [T](next.md)

Get the next [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) from the query, and extract it with the given result set extractor.

#### Throws

| | |
|---|---|
| [NoSuchElementException](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-no-such-element-exception/index.html) | if there are no more results. |

[jvm]\
fun &lt;[T](next.md)&gt; [next](next.md)(rm: [RowMapper](../-row-mapper/index.md)&lt;[T](next.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](next.md)&gt;&gt;

Get the next [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) from the query, and map it with the given row mapper.

Throws [NoSuchElementException](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-no-such-element-exception/index.html) if there are no more results.