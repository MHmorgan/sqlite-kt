//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[MultiResult](index.md)/[all](all.md)

# all

[jvm]\
fun &lt;[T](all.md)&gt; [all](all.md)(rse: [ResultSetExtractor](../-result-set-extractor/index.md)&lt;[T](all.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[T](all.md)&gt;

Get all [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)s from the query, extracting them with the given result set extractor. Returns a list of the extracted results.

[jvm]\
fun &lt;[T](all.md)&gt; [all](all.md)(rm: [RowMapper](../-row-mapper/index.md)&lt;[T](all.md)&gt;): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](all.md)&gt;&gt;

Get all [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)s from the query, mapping them with the given row mapper. Returns a flattened list of the mapped results.