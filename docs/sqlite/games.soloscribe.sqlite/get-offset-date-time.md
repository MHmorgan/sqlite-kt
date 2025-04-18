//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getOffsetDateTime](get-offset-date-time.md)

# getOffsetDateTime

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getOffsetDateTime](get-offset-date-time.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [OffsetDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html)?

Get an [OffsetDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html) from the result set. The date is parsed using the [formatter](get-offset-date-time.md) provided.

#### See also

| | |
|---|---|
| [OffsetDateTime.parse](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html#parse-kotlin.CharSequence-) | for more information on the format. |
