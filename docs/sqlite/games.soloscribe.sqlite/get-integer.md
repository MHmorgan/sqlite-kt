//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getInteger](get-integer.md)

# getInteger

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getInteger](get-integer.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)?

Get an integer from the result set (with better null-handling than [ResultSet.getInt](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getInt-kotlin.Int-)).

#### Throws

| | |
|---|---|
| [NumberFormatException](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-number-format-exception/index.html) | if the column value is not a valid integer. |