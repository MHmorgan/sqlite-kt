//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getOrThrow](get-or-throw.md)

# getOrThrow

[jvm]\
fun &lt;[T](get-or-throw.md)&gt; [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/index.html)&lt;[T](get-or-throw.md)&gt;&gt;.[getOrThrow](get-or-throw.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[T](get-or-throw.md)&gt;

Unwrap all the results in the list. Throws an exception if any of the results are a failure, otherwise returns a list of the unwrapped results.

Throws the first encountered exception, with all other exceptions added as suppressed exceptions.

#### Throws

| |
|---|
| [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-throwable/index.html) |
