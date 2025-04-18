//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getZonedDateTime](get-zoned-date-time.md)

# getZonedDateTime

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getZonedDateTime](get-zoned-date-time.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [ZonedDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html)?

Get a [ZonedDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html) from the result set. The date is parsed using the [formatter](get-zoned-date-time.md) provided.

#### See also

| | |
|---|---|
| [ZonedDateTime.parse](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html#parse-kotlin.CharSequence-) | for more information on the format. |
