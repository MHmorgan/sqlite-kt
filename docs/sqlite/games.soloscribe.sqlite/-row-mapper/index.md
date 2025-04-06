//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[RowMapper](index.md)

# RowMapper

[jvm]\
fun interface [RowMapper](index.md)&lt;[T](index.md)&gt;

The interface used by [SQLite](../-s-q-lite/index.md) for mapping rows of a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)

## Functions

| Name | Summary |
|---|---|
| [map](map.md) | [jvm]<br>abstract fun [map](map.md)(rs: [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html), rowNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)): [T](index.md)<br>Map a single row of data from a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) to an object. This method must not call [ResultSet.next](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#next--) or [ResultSet.close](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#close--). |
| [mapAll](map-all.md) | [jvm]<br>open fun [mapAll](map-all.md)(rs: [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](index.md)&gt;&gt;<br>Map all rows of data from a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) to a list of objects. |