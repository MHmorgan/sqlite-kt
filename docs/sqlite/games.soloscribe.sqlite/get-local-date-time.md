//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getLocalDateTime](get-local-date-time.md)

# getLocalDateTime

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getLocalDateTime](get-local-date-time.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [LocalDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html)?

Get a [LocalDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html) from the result set. The date is parsed using the [formatter](get-local-date-time.md) provided.

#### See also

| | |
|---|---|
| [LocalDateTime.parse](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html#parse-kotlin.CharSequence-) | for more information on the format. |
