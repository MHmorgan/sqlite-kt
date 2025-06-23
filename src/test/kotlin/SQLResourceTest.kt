package dev.hirth.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet

class SQLResourceTest {

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
    fun `SQLResource tester`() = withDatabase { db ->
        val resource = UserResource(db)

        val users = listOf(
            User(1, "Alice", 25),
            User(2, "Bob", 30),
            User(3, "Charlie", 35),
            User(4, "Diana", 28)
        )
        val tester = SQLResourceTester(users) { it.id }
        tester.test(resource)
    }

    @Nested
    inner class BasicOperations {

        @Test
        fun `invoke() should return all items`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")
            db.execute("INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)")

            // Test invoke()
            val users = resource()

            assertThat(users).hasSize(2)
            assertThat(users).containsExactlyInAnyOrder(
                User(1, "Alice", 25),
                User(2, "Bob", 30)
            )
        }

        @Test
        fun `invoke() should return empty list when no items exist`() = withDatabase { db ->
            val resource = UserResource(db)

            val users = resource()

            assertThat(users).isEmpty()
        }

        @Test
        fun `invoke with block should modify query`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")
            db.execute("INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)")
            db.execute("INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 35)")

            // Test invoke with WHERE clause
            val users = resource {
                +"WHERE age > 25"
            }

            assertThat(users).hasSize(2)
            assertThat(users).containsExactlyInAnyOrder(
                User(2, "Bob", 30),
                User(3, "Charlie", 35)
            )
        }

        @Test
        fun `invoke with block and parameters should work`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")
            db.execute("INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)")
            db.execute("INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 35)")

            // Test invoke with parameterized WHERE clause
            val users = resource {
                +"WHERE age > :minAge"
                setParam("minAge", 25)
            }

            assertThat(users).hasSize(2)
            assertThat(users).containsExactlyInAnyOrder(
                User(2, "Bob", 30),
                User(3, "Charlie", 35)
            )
        }

        @Test
        fun `get should return item by id`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")
            db.execute("INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)")

            // Test get existing item
            val user = resource[1]

            assertThat(user).isEqualTo(User(1, "Alice", 25))
        }

        @Test
        fun `get should return null for non-existent id`() = withDatabase { db ->
            val resource = UserResource(db)

            // Test get non-existent item
            val user = resource[999]

            assertThat(user).isNull()
        }

        @Test
        fun `contains should return true for existing id`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")

            // Test contains existing item
            assertThat(1 in resource).isTrue()
        }

        @Test
        fun `contains should return false for non-existent id`() = withDatabase { db ->
            val resource = UserResource(db)

            // Test contains non-existent item
            assertThat(999 in resource).isFalse()
        }
    }

    @Nested
    inner class InsertOperations {

        @Test
        fun `insert should add new item and return affected rows`() = withDatabase { db ->
            val resource = UserResource(db)
            val user = User(1, "Alice", 25)

            // Test insert
            val affectedRows = resource.insert(user)

            assertThat(affectedRows).isEqualTo(1)

            // Verify item was inserted
            val retrievedUser = resource[1]
            assertThat(retrievedUser).isEqualTo(user)
        }

        @Test
        fun `insert should replace existing item`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert initial item
            val originalUser = User(1, "Alice", 25)
            resource.insert(originalUser)

            // Insert replacement item with same id
            val updatedUser = User(1, "Alice Updated", 26)
            val affectedRows = resource.insert(updatedUser)

            assertThat(affectedRows).isEqualTo(1)

            // Verify item was replaced
            val retrievedUser = resource[1]
            assertThat(retrievedUser).isEqualTo(updatedUser)
        }

        @Test
        fun `plusAssign operator should insert item`() = withDatabase { db ->
            val resource = UserResource(db)
            val user = User(1, "Alice", 25)

            // Test plusAssign operator
            resource += user

            // Verify item was inserted
            val retrievedUser = resource[1]
            assertThat(retrievedUser).isEqualTo(user)
        }
    }

    @Nested
    inner class DeleteOperations {

        @Test
        fun `delete should remove existing item and return affected rows`() = withDatabase { db ->
            val resource = UserResource(db)
            val user = User(1, "Alice", 25)

            // Insert item first
            resource.insert(user)
            assertThat(resource[1]).isNotNull()

            // Test delete
            val affectedRows = resource.delete(1)

            assertThat(affectedRows).isEqualTo(1)

            // Verify item was deleted
            assertThat(resource[1]).isNull()
        }

        @Test
        fun `delete should return 0 for non-existent item`() = withDatabase { db ->
            val resource = UserResource(db)

            // Test delete non-existent item
            val affectedRows = resource.delete(999)

            assertThat(affectedRows).isEqualTo(0)
        }

        @Test
        fun `minusAssign operator should delete item`() = withDatabase { db ->
            val resource = UserResource(db)
            val user = User(1, "Alice", 25)

            // Insert item first
            resource.insert(user)
            assertThat(resource[1]).isNotNull()

            // Test minusAssign operator
            resource -= 1

            // Verify item was deleted
            assertThat(resource[1]).isNull()
        }
    }

    @Nested
    inner class EdgeCases {

        @Test
        fun `operations should work with empty table`() = withDatabase { db ->
            val resource = UserResource(db)

            // Test all operations on empty table
            assertThat(resource()).isEmpty()
            assertThat(resource[1]).isNull()
            assertThat(1 in resource).isFalse()
            assertThat(resource.delete(1)).isEqualTo(0)
        }

        @Test
        fun `operations should work with multiple items`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert multiple items
            val users = listOf(
                User(1, "Alice", 25),
                User(2, "Bob", 30),
                User(3, "Charlie", 35),
                User(4, "Diana", 28)
            )

            users.forEach { resource.insert(it) }

            // Test getting all items
            val allUsers = resource()
            assertThat(allUsers).hasSize(4)
            assertThat(allUsers).containsExactlyInAnyOrderElementsOf(users)

            // Test getting specific items
            assertThat(resource[2]).isEqualTo(User(2, "Bob", 30))
            assertThat(resource[4]).isEqualTo(User(4, "Diana", 28))

            // Test contains
            assertThat(2 in resource).isTrue()
            assertThat(5 in resource).isFalse()

            // Test delete
            resource.delete(2)
            assertThat(resource()).hasSize(3)
            assertThat(2 in resource).isFalse()
        }

        @Test
        fun `invoke with complex SQL modification should work`() = withDatabase { db ->
            val resource = UserResource(db)

            // Insert test data
            db.execute("INSERT INTO users (id, name, age) VALUES (1, 'Alice', 25)")
            db.execute("INSERT INTO users (id, name, age) VALUES (2, 'Bob', 30)")
            db.execute("INSERT INTO users (id, name, age) VALUES (3, 'Charlie', 35)")
            db.execute("INSERT INTO users (id, name, age) VALUES (4, 'Diana', 28)")

            // Test complex query modification
            val users = resource {
                +"WHERE age BETWEEN :minAge AND :maxAge"
                +"ORDER BY name"
                setParam("minAge", 25)
                setParam("maxAge", 30)
            }

            assertThat(users).hasSize(3)
            // Should be ordered by name: Alice, Bob, Diana
            assertThat(users).containsExactly(
                User(1, "Alice", 25),
                User(2, "Bob", 30),
                User(4, "Diana", 28)
            )
        }
    }
}
