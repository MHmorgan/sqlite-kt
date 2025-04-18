//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[RowMapper](index.md)/[map](map.md)

# map

[jvm]\
abstract fun [map](map.md)(rs: [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html), rowNum: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [T](index.md)

Map a single row of data from a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) to an object. This method must not call [ResultSet.next](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#next--) or [ResultSet.close](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#close--).
