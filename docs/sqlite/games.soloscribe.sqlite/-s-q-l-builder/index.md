//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLBuilder](index.md)

# SQLBuilder

[jvm]\
class [SQLBuilder](index.md)

A sql query builder. This provides a convenient way of building sql queries with named parameters in a flexible way.

## Constructors

| | |
|---|---|
| [SQLBuilder](-s-q-l-builder.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [params](params.md) | [jvm]<br>fun [params](params.md)(): &lt;Error class: unknown class&gt; |
| [setParam](set-param.md) | [jvm]<br>fun [setParam](set-param.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?)<br>Set the query parameter with [name](set-param.md) to the given [value](set-param.md). |
| [sql](sql.md) | [jvm]<br>fun [sql](sql.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) |
| [unaryPlus](unary-plus.md) | [jvm]<br>operator fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html).[unaryPlus](unary-plus.md)(): [StringBuilder](https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html) |
| [withCte](with-cte.md) | [jvm]<br>fun [withCte](with-cte.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), block: [SQLBuilder](index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))<br>Add a common table expression (CTE) with the given [name](with-cte.md) and [block](with-cte.md). |
