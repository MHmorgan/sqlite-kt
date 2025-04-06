//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getEnum](get-enum.md)

# getEnum

[jvm]\
fun &lt;[T](get-enum.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[T](get-enum.md)&gt;&gt; [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getEnum](get-enum.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clazz: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)&lt;[T](get-enum.md)&gt;): [T](get-enum.md)?

fun &lt;[T](get-enum.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[T](get-enum.md)&gt;&gt; [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getEnum](get-enum.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clazz: [KClass](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](get-enum.md)&gt;): [T](get-enum.md)?

Get an enum from the result set. [Enum.name](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/name.html) is used to match the enum constant.

#### Throws

| | |
|---|---|
| [NoSuchElementException](https://docs.oracle.com/javase/8/docs/api/java/util/NoSuchElementException.html) | if the enum constant is not found. |