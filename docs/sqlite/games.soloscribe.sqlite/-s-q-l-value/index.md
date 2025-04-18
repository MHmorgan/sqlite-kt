//[sqlite](../../../index.md)/[games.soloscribe.sqlite](../index.md)/[SQLValue](index.md)

# SQLValue

[jvm]\
interface [SQLValue](index.md)&lt;[T](index.md)&gt;

[SQLValue](index.md) should be implemented by classes which can be converted to a type that can be understood by the SQL driver.

Any class implementing this interface can be supplied as a parameter to [SQLite](../-s-q-lite/index.md) statements. The value will be extracted from the object and used in the query. This of course means that the value returned by [sqlValue](sql-value.md) must be a valid SQL value, e.g. the database driver must know how to handle it.

## Functions

| Name | Summary |
|---|---|
| [sqlValue](sql-value.md) | [jvm]<br>abstract fun [sqlValue](sql-value.md)(): [T](index.md) |
