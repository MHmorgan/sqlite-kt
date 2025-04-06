//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[MultiResult](index.md)

# MultiResult

[jvm]\
class [MultiResult](index.md)(moreResults: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), ps: [PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html))

A helper class for extracting multiple results from a query.

It provides methods (a tiny DDL) for extracting result sets by using [ResultSetExtractor](../-result-set-extractor/index.md)s and [RowMapper](../-row-mapper/index.md)s.

## Constructors

| | |
|---|---|
| [MultiResult](-multi-result.md) | [jvm]<br>constructor(moreResults: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), ps: [PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)) |

## Functions

| Name | Summary |
|---|---|
| [all](all.md) | [jvm]<br>fun &lt;[T](all.md)&gt; [all](all.md)(rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](all.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[T](all.md)&gt;<br>Get all [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)s from the query, extracting them with the given result set extractor. Returns a list of the extracted results.<br>[jvm]<br>fun &lt;[T](all.md)&gt; [all](all.md)(rm: [RowMapper](../-row-mapper/index.md)&lt;[T](all.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](all.md)&gt;&gt;<br>Get all [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)s from the query, mapping them with the given row mapper. Returns a flattened list of the mapped results. |
| [next](next.md) | [jvm]<br>fun &lt;[T](next.md)&gt; [next](next.md)(rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](next.md)&gt;): [T](next.md)<br>Get the next [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) from the query, and extract it with the given result set extractor.<br>[jvm]<br>fun &lt;[T](next.md)&gt; [next](next.md)(rm: [RowMapper](../-row-mapper/index.md)&lt;[T](next.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](next.md)&gt;&gt;<br>Get the next [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) from the query, and map it with the given row mapper. |