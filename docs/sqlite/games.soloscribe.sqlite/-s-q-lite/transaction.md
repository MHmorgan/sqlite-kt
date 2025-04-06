//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLite](index.md)/[transaction](transaction.md)

# transaction

[jvm]\
fun &lt;[T](transaction.md)&gt; [transaction](transaction.md)(func: () -&gt; [T](transaction.md)): [T](transaction.md)

Run the given [func](transaction.md) inside a transaction, committing if it succeeds and rolling back if it fails.