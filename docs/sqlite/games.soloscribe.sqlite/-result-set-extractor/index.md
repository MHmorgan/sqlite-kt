//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[ResultSetExtractor](index.md)

# ResultSetExtractor

[jvm]\
fun interface [ResultSetExtractor](index.md)&lt;[T](index.md)&gt;

The interface used by [SQLite](../-s-q-lite/index.md) for extracting a result from an entire [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).

## Functions

| Name | Summary |
|---|---|
| [extract](extract.md) | [jvm]<br>abstract fun [extract](extract.md)(rs: [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)): [T](index.md)<br>Extract data from a [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html). |
