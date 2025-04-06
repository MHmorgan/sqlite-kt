//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getOffsetTime](get-offset-time.md)

# getOffsetTime

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getOffsetTime](get-offset-time.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [OffsetTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html)?

Get an [OffsetTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html) from the result set. The time is parsed using the [formatter](get-offset-time.md) provided.

#### See also

| | |
|---|---|
| [OffsetTime.parse](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html#parse-kotlin.CharSequence-) | for more information on the format. |