//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)/[multiQuery](multi-query.md)

# multiQuery

[jvm]\
fun &lt;[T](multi-query.md)&gt; [multiQuery](multi-query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md), block: [MultiResult](../-multi-result/index.md).() -&gt; [T](multi-query.md)): [T](multi-query.md)

Execute all the sql statements from src with [params](multi-query.md). This should be used when there are multiple queries in src, which requires multiple result sets to be extracted.

The results are extracted using calling the extension [block](multi-query.md) on a [MultiResult](../-multi-result/index.md) which allows the caller to control the extraction of multiple result sets.

[jvm]\
fun &lt;[T](multi-query.md)&gt; [multiQuery](multi-query.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), block: [MultiResult](../-multi-result/index.md).() -&gt; [T](multi-query.md)): [T](multi-query.md)

Execute all the sql statements from [sql](multi-query.md). This should be used when there are multiple queries in [sql](multi-query.md), which requires multiple result sets to be extracted.

The results are extracted using calling the extension [block](multi-query.md) on a [MultiResult](../-multi-result/index.md) which allows the caller to control the extraction of multiple result sets.