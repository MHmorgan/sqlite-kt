Kotlin SQLite
=============

[Documentation](docs/index.md)

Helper library for SQLite database in Kotlin, only aimed at the JVM platform.

This is tested using `org.xerial.sqlite-jdbc`, but does not include any
driver dependency runtime. The user must import the desired driver as a
dependency, in addition to this library.

Usage
-----

```kotlin
import games.soloscribe.sqlite.SQLite

fun main() {
    val config = SQLite.Config("jdbc:sqlite::memory:", "test-db")

    SQLite(config).use { db ->
        db.execute("CREATE TABLE test (name)")
        
        val names = listOf(
            mapOf("name" to "Alice"),
            mapOf("name" to "Bob"),
        )
        db.batchUpdate("INSERT INTO test (name) VALUES (:name)", names)
        
        val res = db.query("SELECT * FROM test") { rs, _ ->
            rs.getString("name")
        }
        for (name in res)
            println(name)
    }
}
```
