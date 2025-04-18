//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getSingleOrNull](get-single-or-null.md)

# getSingleOrNull

[jvm]\
fun &lt;[T](get-single-or-null.md)&gt; [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](get-single-or-null.md)&gt;&gt;.[getSingleOrNull](get-single-or-null.md)(): [T](get-single-or-null.md)?

Unwrap the result of the list, expecting a single result. Returns `null` if the list is empty, or if it contains more than one result.

Throws an exception if it encounters any failures in the list.

#### See also

| |
|---|
| singleOrNull |
| [getOrThrow](get-or-throw.md) |
