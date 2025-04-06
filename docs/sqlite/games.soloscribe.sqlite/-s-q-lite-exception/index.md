//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLiteException](index.md)

# SQLiteException

[jvm]\
open class [SQLiteException](index.md)(msg: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), cause: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) : [SQLException](https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html)

## Constructors

| | |
|---|---|
| [SQLiteException](-s-q-lite-exception.md) | [jvm]<br>constructor(msg: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), cause: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [cause](index.md#-654012527%2FProperties%2F-1216412040) | [jvm]<br>open val [cause](index.md#-654012527%2FProperties%2F-1216412040): [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? |
| [message](index.md#1824300659%2FProperties%2F-1216412040) | [jvm]<br>open val [message](index.md#1824300659%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [getErrorCode](index.md#-142184163%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getErrorCode](index.md#-142184163%2FFunctions%2F-1216412040)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [getNextException](index.md#324451830%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getNextException](index.md#324451830%2FFunctions%2F-1216412040)(): [SQLException](https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html) |
| [getSQLState](index.md#151790405%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getSQLState](index.md#151790405%2FFunctions%2F-1216412040)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [iterator](index.md#2044596334%2FFunctions%2F-1216412040) | [jvm]<br>open operator override fun [iterator](index.md#2044596334%2FFunctions%2F-1216412040)(): [MutableIterator](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-mutable-iterator/index.html)&lt;[Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)&gt; |
| [setNextException](index.md#23595891%2FFunctions%2F-1216412040) | [jvm]<br>open fun [setNextException](index.md#23595891%2FFunctions%2F-1216412040)(p0: [SQLException](https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html)) |