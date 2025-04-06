//[sqlite](../../index.md)/[games.soloscribe.sqlite](index.md)/[getZoneId](get-zone-id.md)

# getZoneId

[jvm]\
fun [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html).[getZoneId](get-zone-id.md)(column: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [ZoneId](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html)?

Get a [ZoneId](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html) from the result set. The zone ID is parsed using the formatter provided.

#### See also

| | |
|---|---|
| [ZoneId.of](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-kotlin.String-kotlin.collections.MutableMap[kotlin.String,kotlin.String]-) | for more information on the format. |