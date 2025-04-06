//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getLocalTime](get-local-time.md)

# getLocalTime

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getLocalTime](get-local-time.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)?

Get a [LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html) from the result set. The time is parsed using the [formatter](get-local-time.md) provided.

#### See also

| | |
|---|---|
| [LocalTime.parse](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html#parse-kotlin.CharSequence-) | for more information on the format. |