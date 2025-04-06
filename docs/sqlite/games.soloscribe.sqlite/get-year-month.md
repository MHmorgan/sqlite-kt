//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getYearMonth](get-year-month.md)

# getYearMonth

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getYearMonth](get-year-month.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [YearMonth](https://docs.oracle.com/javase/8/docs/api/java/time/YearMonth.html)?

Get a [YearMonth](https://docs.oracle.com/javase/8/docs/api/java/time/YearMonth.html) from the result set. The date is parsed using the [formatter](get-year-month.md) provided.

#### See also

| | |
|---|---|
| [YearMonth.parse](https://docs.oracle.com/javase/8/docs/api/java/time/YearMonth.html#parse-kotlin.CharSequence-) | for more information on the format. |