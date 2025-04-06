//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)/[batchUpdate](batch-update.md)

# batchUpdate

[jvm]\
fun [batchUpdate](batch-update.md)(sql: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), batches: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[SQLParams](../-s-q-l-params/index.md)&gt;): [IntArray](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int-array/index.html)

Execute the sql statement from [sql](batch-update.md) with the [batches](batch-update.md) of named parameters and return an array with the number of affected rows for each batch.