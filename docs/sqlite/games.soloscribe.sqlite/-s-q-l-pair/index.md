//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLPair](index.md)

# SQLPair

[jvm]\
class [SQLPair](index.md)(first: [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?, second: [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?) : [SQLCollection](../-s-q-l-collection/index.md)

[SQLPair](index.md) is a collection of items which should be expanded to a parenthesis enclosed pair in a SQL query.

Example: `SqlPair(1, 2)` expands to `(?,?)`

## Constructors

| | |
|---|---|
| [SQLPair](-s-q-l-pair.md) | [jvm]<br>constructor(pair: [Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html)&lt;[Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?, [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;)constructor(first: [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?, second: [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [size](../-s-q-l-collection/size.md) | [jvm]<br>val [size](../-s-q-l-collection/size.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toString](../-s-q-l-collection/to-string.md) | [jvm]<br>open override fun [toString](../-s-q-l-collection/to-string.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |