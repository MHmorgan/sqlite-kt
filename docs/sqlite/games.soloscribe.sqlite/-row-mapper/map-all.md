//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[RowMapper](index.md)/[mapAll](map-all.md)

# mapAll

[jvm]\
open fun [mapAll](map-all.md)(rs: [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](index.md)&gt;&gt;

Map all rows of data from a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) to a list of objects.

The results of [map](map.md) are wrapped in a [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html) to allow for cleaner handling of errors, since the first encountered error won't stop the entire mapping process.
