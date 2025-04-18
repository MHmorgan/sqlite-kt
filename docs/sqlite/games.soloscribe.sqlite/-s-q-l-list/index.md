//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLList](index.md)

# SQLList

[jvm]\
class [SQLList](index.md)(items: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?&gt;) : [SQLCollection](../-s-q-l-collection/index.md)

[SQLList](index.md) is a collection of items which should be expanded to a comma-separated list in a SQL query.

Example: `SqlList(1, 2, 3)` expands to `?,?,?`

The difference between using [SQLList](index.md) and [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html) is that [SQLList](index.md) is recursively expanded, meaning that if items contains other [SQLCollection](../-s-q-l-collection/index.md)s they will be expanded as well.

## Constructors

| | |
|---|---|
| [SQLList](-s-q-l-list.md) | [jvm]<br>constructor(vararg items: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?)constructor(items: [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-collection/index.html)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?&gt;)constructor(items: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?&gt;) |

## Properties

| Name | Summary |
|---|---|
| [size](../-s-q-l-collection/size.md) | [jvm]<br>val [size](../-s-q-l-collection/size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [toString](../-s-q-l-collection/to-string.md) | [jvm]<br>open override fun [toString](../-s-q-l-collection/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) |
