//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLCollection](index.md)

# SQLCollection

sealed class [SQLCollection](index.md)

[SQLCollection](index.md) is a base class for collections used as parameters in SQL queries.

[SQLCollection](index.md)s are purely semantic classes which convey explicit information about how the collection parameters should be handled, which provide flexibility and makes it easy to build complex queries. The explicit solution is, as always, superior to any implicit solution by eliminating a range of edge cases and limitations.

All sql collections are recursively expanded.

Example of a complicated query:

```kotlin
SqlList("One", SqlPair(1, 2), List(SqlPair(3, 4)), SqlList(5, 6))
```

Which expands to:

```kotlin
val resolvedSql = "?,(?,?),?,?,?"
val resolvedParams = arrayOf("One", 1, 2, SqlPair(3, 4), 5, 6)
```

What happens here is:

1. 
   `"One"` is expanded to `?`
2. 
   `SqlPair(1, 2)` is expanded to `(?,?)`
3. 
   `List(SqlPair(3, 4))` is expanded to `?`     (because normal lists aren't recursively expanded)
4. 
   `SqlList(5, 6)` is expanded to `?,?`

#### Inheritors

| |
|---|
| [SQLList](../-s-q-l-list/index.md) |
| [SQLPair](../-s-q-l-pair/index.md) |

## Properties

| Name | Summary |
|---|---|
| [size](size.md) | [jvm]<br>val [size](size.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |