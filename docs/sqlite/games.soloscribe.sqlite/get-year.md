//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getYear](get-year.md)

# getYear

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getYear](get-year.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [Year](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html)?

Get a [Year](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html) from the result set. The year is parsed using the [formatter](get-year.md) provided.

#### See also

| | |
|---|---|
| [Year.parse](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html#parse-kotlin.CharSequence-) | for more information on the format. |