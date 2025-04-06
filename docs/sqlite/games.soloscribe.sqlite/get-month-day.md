//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getMonthDay](get-month-day.md)

# getMonthDay

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getMonthDay](get-month-day.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), formatter: [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)? = null): [MonthDay](https://docs.oracle.com/javase/8/docs/api/java/time/MonthDay.html)?

Get a [MonthDay](https://docs.oracle.com/javase/8/docs/api/java/time/MonthDay.html) from the result set. The date is parsed using the [formatter](get-month-day.md) provided.

#### See also

| | |
|---|---|
| [MonthDay.parse](https://docs.oracle.com/javase/8/docs/api/java/time/MonthDay.html#parse-kotlin.CharSequence-) | for more information on the format. |