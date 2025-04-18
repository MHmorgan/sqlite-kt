//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getUUID](get-u-u-i-d.md)

# getUUID

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getUUID](get-u-u-i-d.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html)?

Get a [UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html) object from the result set. This does not require the database driver to support UUIDs, and works with any database.
