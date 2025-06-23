🗃️ Kotlin SQLite
================

A comprehensive helper library for SQLite database operations in Kotlin, designed specifically for the JVM platform.

This library is tested using `org.xerial.sqlite-jdbc`, but does not include any driver dependency at runtime.
Users must import their desired SQLite JDBC driver as a dependency alongside this library.

## ✨ Features

- 🛡️ **Type-safe database operations** with comprehensive type conversion utilities
- 🏷️ **Named parameter support** for SQL queries with automatic parameter parsing
- 🏗️ **SQL Builder DSL** for dynamic query construction
- 🗂️ **Resource abstraction** for object-relational mapping patterns
- 💸 **Transaction management** with automatic rollback on exceptions
- ⚡ **Batch operations** for efficient bulk data processing
- 🎯 **Extensive type support** including Java time types, UUIDs, enums, and JSON
- 📦 **SQL Collections** for complex parameter handling (lists, pairs)

## 🚀 Quick Start

```kotlin
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

## 💡 Core Components

### 🏛️ SQLite Class

The main database interface providing:

- 🔍 **Query execution**: `query()`, `execute()`, `batchUpdate()`
- 💰 **Transaction management**: `transaction { ... }`
- 🏗️ **Schema operations**: `schema()`, `checkForeignKeys()`
- 🧹 **Resource management**: Implements `AutoCloseable`

```kotlin
val config = SQLite.Config(dataSource, "my-db")
SQLite(config).use { db ->
    // Database operations
    db.transaction {
        db.execute("INSERT INTO users (name) VALUES (:name)", mapOf("name" to "Alice"))
        db.execute("INSERT INTO posts (user_id, title) VALUES (:userId, :title)", 
                  mapOf("userId" to 1, "title" to "Hello World"))
    }
}
```

### 🏷️ Named Parameters

All SQL operations support named parameters using `:paramName` syntax:

```kotlin
val params = mapOf(
    "name" to "Alice",
    "age" to 30,
    "active" to true
)
db.execute("INSERT INTO users (name, age, active) VALUES (:name, :age, :active)", params)

val user = db.query("SELECT * FROM users WHERE name = :name", mapOf("name" to "Alice")) { rs, _ ->
    User(rs.getString("name"), rs.getInt("age"), rs.getBoolean("active"))
}.getSingleOrNull()
```

### 🏗️ SQL Builder DSL

Build dynamic queries with a type-safe DSL:

```kotlin
val (sql, params) = buildQuery {
    +"SELECT * FROM users WHERE 1=1"

    if (nameFilter != null) {
        +"AND name LIKE :namePattern"
        setParam("namePattern", "%$nameFilter%")
    }

    if (ageMin != null) {
        +"AND age >= :ageMin"
        setParam("ageMin", ageMin)
    }

    +"ORDER BY name"
}

val users = db.query(sql, params) { rs, _ -> 
    User(rs.getString("name"), rs.getInt("age"))
}
```

### 🗂️ SQLResource - Object-Relational Mapping

Abstract base class for table-backed resources with CRUD operations:

```kotlin
class UserResource(db: SQLite, log: Logger) : SQLResource<Int, User>("users", log, db) {
    override val selectAllSql = "SELECT * FROM users"
    override val selectByIdSql = "SELECT * FROM users WHERE id = :id"
    override val insertSql = "INSERT OR REPLACE INTO users (id, name, age) VALUES (:id, :name, :age)"
    override val deleteSql = "DELETE FROM users WHERE id = :id"

    override fun params(item: User) = mapOf(
        "id" to item.id,
        "name" to item.name,
        "age" to item.age
    )

    override fun rowMapper(rs: ResultSet, lineNum: Int) = User(
        rs.getInt("id"),
        rs.getString("name"),
        rs.getInt("age")
    )
}

// Usage with operator overloads
val users = UserResource(db, logger)

// Get all users
val allUsers = users()

// Get specific user
val alice = users[1]

// Check existence
if (1 in users) {
    println("User exists")
}

// Insert user
users += User(2, "Bob", 25)

// Delete user
users -= 1
```

### 🎯 Type Conversion Utilities

Extensive support for converting database values to Kotlin/Java types:

```kotlin
val result = db.query("SELECT * FROM events") { rs, _ ->
    Event(
        id = rs.getInt("id"),
        name = rs.getString("name"),
        createdAt = rs.getLocalDateTime("created_at"),
        eventDate = rs.getLocalDate("event_date"),
        duration = rs.getDuration("duration"),
        timezone = rs.getZoneId("timezone"),
        uuid = rs.getUuid("uuid"),
        status = rs.getEnum("status", EventStatus::class),
        metadata = rs.getJson<Map<String, String>>("metadata")
    )
}
```

Supported types include:
- 📅 **Date/Time**: `LocalDateTime`, `LocalDate`, `LocalTime`, `OffsetDateTime`, `ZonedDateTime`, etc.
- 🔑 **UUIDs**: Both Kotlin `Uuid` and Java `UUID`
- 🏷️ **Enums**: Type-safe enum conversion
- 📋 **JSON**: Automatic serialization/deserialization with kotlinx.serialization
- 🎨 **Custom types**: Via `SQLValue` interface

### 📦 SQL Collections

Handle complex parameter structures with specialized collection types:

```kotlin
// SQLList for comma-separated values
val userIds = SQLList(1, 2, 3, 4)
db.query("SELECT * FROM users WHERE id IN (:ids)", 
         mapOf("ids" to userIds)) { rs, _ -> /* ... */ }

// SQLPair for tuple-like parameters
val coordinate = SQLPair(latitude, longitude)
db.execute("INSERT INTO locations (coords) VALUES :coords", 
           mapOf("coords" to coordinate))

// Complex nested structures
val complexParam = SQLList("Alice", SQLPair(1, 2), listOf(3, 4), SQLList(5, 6))
// Resolves to: "?,(?,?),?,?,?" with parameters ["Alice", 1, 2, [3,4], 5, 6]
```

### 🎨 Custom Value Types

Implement `SQLValue` interface for custom type conversion:

```kotlin
enum class UserRole(private val dbValue: String) : SQLValue<String> {
    ADMIN("admin"),
    USER("user"),
    GUEST("guest");

    override fun sqlValue() = dbValue
}

// Usage
db.execute("INSERT INTO users (name, role) VALUES (:name, :role)", 
           mapOf("name" to "Alice", "role" to UserRole.ADMIN))
```

### 💸 Transaction Management

Automatic transaction handling with rollback on exceptions:

```kotlin
db.transaction {
    db.execute("INSERT INTO users (name) VALUES (:name)", mapOf("name" to "Alice"))
    db.execute("INSERT INTO profiles (user_id, bio) VALUES (:userId, :bio)", 
               mapOf("userId" to 1, "bio" to "Software Developer"))

    // If any operation fails, entire transaction is rolled back
    if (someCondition) {
        throw RuntimeException("Rollback transaction")
    }
}
```

### ⚡ Batch Operations

Efficient bulk data processing:

```kotlin
val users = listOf(
    mapOf("name" to "Alice", "age" to 30),
    mapOf("name" to "Bob", "age" to 25),
    mapOf("name" to "Charlie", "age" to 35)
)

val results = db.batchUpdate("INSERT INTO users (name, age) VALUES (:name, :age)", users)
println("Inserted ${results.sum()} users")
```

### 🔍 Schema Introspection

Inspect database schema programmatically:

```kotlin
val schema = db.schema()
for (item in schema) {
    println("${item.type}: ${item.name}")
    if (item.isTable) {
        println("  SQL: ${item.sql}")
    }
}

// Check foreign key constraints
db.checkForeignKeys()
```

## 🔧 Configuration

### 🛠️ Basic Configuration

```kotlin
// Using JDBC URL
val config = SQLite.Config("jdbc:sqlite:path/to/database.db", "my-app")

// Using DataSource
val dataSource = SQLiteDataSource().apply {
    url = "jdbc:sqlite::memory:"
}
val config = SQLite.Config(dataSource, "my-app")
```

## 🛡️ Error Handling

The library provides `SQLiteException` for database-specific errors:

```kotlin
try {
    db.execute("INVALID SQL")
} catch (e: SQLiteException) {
    logger.error("Database error: ${e.message}", e)
}
```

## 🧪 Testing Support

The library includes `SQLResourceTester` for comprehensive testing of `SQLResource` implementations:

```kotlin
@Test
fun testUserResource() {
    val testUsers = listOf(
        User(1, "Alice", 30),
        User(2, "Bob", 25)
    )

    val tester = SQLResourceTester(testUsers) { it.id }
    tester.test(userResource)
}
```

## ⚠️ Thread Safety

SQLite connections are not thread-safe. Each thread should use its own `SQLite` instance.
