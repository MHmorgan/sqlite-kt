//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)/[execute](execute.md)

# execute

[jvm]\
fun [execute](execute.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [SQLParams](../-s-q-l-params/index.md)): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)

Execute the [sql](execute.md) statements with named [params](execute.md).

If the [sql](execute.md) contains multiple statements, they are all executed within the same transaction.

[jvm]\
fun [execute](execute.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)

Execute the [sql](execute.md) statements.