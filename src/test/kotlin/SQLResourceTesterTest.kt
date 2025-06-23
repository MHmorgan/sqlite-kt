package dev.hirth.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.sql.ResultSet

class SQLResourceTesterTest {

    // Test data class
    data class User(val id: Int, val name: String, val age: Int)

    // Concrete implementation of SQLResource for testing
    class UserResource(db: SQLite) : SQLResource<Int, User>(
        name = "user",
        log = LoggerFactory.getLogger(UserResource::class.java),
        db = db
    ) {
        override val selectAllSql = "SELECT id, name, age FROM users"
        override val selectByIdSql = "SELECT id, name, age FROM users WHERE id = :id"
        override val insertSql = "INSERT OR REPLACE INTO users (id, name, age) VALUES (:id, :name, :age)"
        override val deleteSql = "DELETE FROM users WHERE id = :id"

        override fun params(item: User): Map<String, Any?> = mapOf(
            "id" to item.id,
            "name" to item.name,
            "age" to item.age
        )

        override fun rowMapper(rs: ResultSet, lineNum: Int): User = User(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            age = rs.getInt("age")
        )
    }

    private fun withDatabase(block: (SQLite) -> Unit) {
        val config = SQLite.Config(
            dataSource = org.sqlite.SQLiteDataSource().apply {
                url = "jdbc:sqlite::memory:"
            },
            name = "test"
        )

        SQLite(config).use { db ->
            // Create test table
            db.execute("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    age INTEGER NOT NULL
                )
            """.trimIndent())

            block(db)
        }
    }

    @Test
    fun `SQLResourceTester should test all SQLResource functionality`() = withDatabase { db ->
        val userResource = UserResource(db)
        
        // Create test data
        val testUsers = listOf(
            User(1, "Alice", 25),
            User(2, "Bob", 30),
            User(3, "Charlie", 35)
        )
        
        // Create the tester
        val tester = SQLResourceTester(
            testObjects = testUsers,
            identifierOf = { user -> user.id }
        )
        
        // Run the comprehensive test
        tester.test(userResource)
        
        // Verify that the resource is clean after testing
        assertThat(userResource()).isEmpty()
    }

    @Test
    fun `SQLResourceTester should work with single test object`() = withDatabase { db ->
        val userResource = UserResource(db)
        
        // Create test data with single object
        val testUsers = listOf(
            User(1, "Alice", 25)
        )
        
        // Create the tester
        val tester = SQLResourceTester(
            testObjects = testUsers,
            identifierOf = { user -> user.id }
        )
        
        // Run the comprehensive test
        tester.test(userResource)
        
        // Verify that the resource is clean after testing
        assertThat(userResource()).isEmpty()
    }

    @Test
    fun `SQLResourceTester should work with string identifiers`() = withDatabase { db ->
        // Create a modified UserResource that uses string IDs
        class StringUserResource(db: SQLite) : SQLResource<String, User>(
            name = "user",
            log = LoggerFactory.getLogger(StringUserResource::class.java),
            db = db
        ) {
            override val selectAllSql = "SELECT id, name, age FROM users"
            override val selectByIdSql = "SELECT id, name, age FROM users WHERE id = :id"
            override val insertSql = "INSERT OR REPLACE INTO users (id, name, age) VALUES (:id, :name, :age)"
            override val deleteSql = "DELETE FROM users WHERE id = :id"

            override fun params(item: User): Map<String, Any?> = mapOf(
                "id" to item.id,
                "name" to item.name,
                "age" to item.age
            )

            override fun rowMapper(rs: ResultSet, lineNum: Int): User = User(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                age = rs.getInt("age")
            )
        }
        
        val userResource = StringUserResource(db)
        
        // Create test data
        val testUsers = listOf(
            User(1, "Alice", 25),
            User(2, "Bob", 30)
        )
        
        // Create the tester with string identifier extraction
        val tester = SQLResourceTester(
            testObjects = testUsers,
            identifierOf = { user -> user.id.toString() }
        )
        
        // Run the comprehensive test
        tester.test(userResource)
        
        // Verify that the resource is clean after testing
        assertThat(userResource()).isEmpty()
    }
}