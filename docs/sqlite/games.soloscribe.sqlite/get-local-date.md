//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getLocalDate](get-local-date.md)

# getLocalDate

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getLocalDate](get-local-date.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)?

Get a [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html) from the result set. The date is parsed using the [formatter](get-local-date.md) provided.

#### See also

| | |
|---|---|
| [LocalDate.parse](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html#parse-kotlin.CharSequence-) | for more information on the format. |
