//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getInteger](get-integer.md)

# getInteger

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getInteger](get-integer.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): &lt;Error class: unknown class&gt;

Get an integer from the result set (with better null-handling than [ResultSet.getInt](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getInt-kotlin.Int-)).

#### Throws

| | |
|---|---|
| [NumberFormatException](https://docs.oracle.com/javase/8/docs/api/java/lang/NumberFormatException.html) | if the column value is not a valid integer. |
