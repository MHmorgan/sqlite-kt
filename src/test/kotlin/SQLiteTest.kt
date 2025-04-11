@file:Suppress("MemberVisibilityCanBePrivate")

package games.soloscribe.sqlite

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.math.BigDecimal
import java.time.*
import java.util.*

class SQLiteTest {

    val config = SQLite.Config(
        url = "jdbc:sqlite::memory:",
        name = "test-db"
    )

    @Test
    fun `Test sqlite_schema`() {
        data class Entry(
            val name: String,
            val type: String,
            val sql: String,
        )

        val entries = listOf(
            Entry(
                name = "character",
                type = "table",
                sql = "CREATE TABLE character (name, age)"
            ),
            Entry(
                name = "profession",
                type = "table",
                sql = "CREATE TABLE profession (name, profession)"
            ),
            Entry(
                name = "name",
                type = "table",
                sql = "CREATE TABLE name (name TEXT)"
            ),
            Entry(
                name = "summary",
                type = "view",
                sql = """
                    CREATE VIEW summary AS
                    SELECT name, age, profession
                    FROM character c
                    JOIN profession p ON c.name = p.name
                """.trimIndent()
            ),
            Entry(
                name = "char_idx",
                type = "index",
                sql = "CREATE INDEX char_idx ON character (name)"
            ),
            Entry(
                name = "char_trg",
                type = "trigger",
                sql = """
                    CREATE TRIGGER char_trg
                    AFTER INSERT ON character
                    BEGIN
                        INSERT INTO name (name) VALUES (new.name);
                    END
                """.trimIndent()
            )
        )

        SQLite(config).use { db ->
            for ((_, _, sql) in entries) db.execute(sql)

            for (actual in db.schema()) {
                val expect = entries.find { it.name == actual.name }
                    ?: Assertions.fail("No entry found for ${actual.name}")
                assertThat(actual.type).isEqualTo(expect.type)
                assertThat(actual.sql).isEqualTo(expect.sql)
                when (expect.type) {
                    "table" -> assertThat(actual.isTable).isTrue()
                    "view" -> assertThat(actual.isView).isTrue()
                    "index" -> assertThat(actual.isIndex).isTrue()
                    "trigger" -> assertThat(actual.isTrigger).isTrue()
                    else -> Assertions.fail("Unknown type: ${expect.type}")
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    //
    // Basics
    //
    // -------------------------------------------------------------------------

    /**
     * The basic tests are just to make sure that no fundamental errors are
     * present. They are not meant to be exhaustive.
     */
    @Nested
    @DisplayName("Basic Tests")
    inner class Basics {

        @Test
        fun `Plain String Queries`() {
            SQLite(config).use { db ->
                val s = "CREATE TABLE test (num)"
                db.execute(s)
                db.execute("INSERT INTO test (num) VALUES (42)")
                db.execute("INSERT INTO test (num) VALUES (8)")
                val sum = db.query("SELECT SUM(num) FROM test") { rs ->
                    rs.next()
                    rs.getInt(1)
                }
                assertThat(sum).isEqualTo(50)
            }
        }

        @Test
        fun `Plain Failing Queries`() {
            SQLite(config).use { db ->
                val s = "CREATE TABLE test (id PRIMARY KEY)"
                db.execute(s)
                assertThrows<SQLiteException> {
                    db.execute(s)
                }
                assertThrows<SQLiteException> {
                    db.execute("INSERT INTO foo (id) VALUES (1)")
                }
            }
        }

        @Test
        fun `Plain String Queries With Multiple Statements`() {
            SQLite(config).use { db ->
                val sql = """
                    CREATE TABLE test (num);
                    CREATE TABLE foo (id INTEGER REFERENCES test (id));
                    INSERT INTO test (num) VALUES (42);
                    INSERT INTO test (num) VALUES (8);
                    INSERT INTO foo (id) VALUES (1);
                """.trimIndent()
                db.execute(sql)
                val sum = db.query("SELECT SUM(num) FROM test") { rs ->
                    rs.next()
                    rs.getInt(1)
                }
                assertThat(sum).isEqualTo(50)
            }
        }

        @Test
        fun `Plain String Queries With Transaction`() {
            SQLite(config).use { db ->
                val sum = db.transaction {
                    db.execute("CREATE TABLE test (num)")

                    var n = db.execute("INSERT INTO test (num) VALUES (42)")
                    assertThat(n).isEqualTo(1)
                    n = db.execute("INSERT INTO test (num) VALUES (8)")
                    assertThat(n).isEqualTo(1)

                    db.query("SELECT SUM(num) FROM test") { rs ->
                        rs.next()
                        rs.getInt(1)
                    }
                }
                assertThat(sum).isEqualTo(50)
            }
        }

        @Test
        fun `Insert with RETURNING`() {
            SQLite(config).use { db ->
                db.execute("CREATE TABLE test (num)")

                val sql = """
                    INSERT INTO test (num)
                    VALUES (42), (69), (1994)
                    RETURNING ROWID, num
                """

                val res = db.query(sql) { rs, _ ->
                    rs.getInt("ROWID") to rs.getInt("num")
                }.getOrThrow()

                assertThat(res).containsExactly(
                    1 to 42,
                    2 to 69,
                    3 to 1994
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    //
    // Types
    //
    // -------------------------------------------------------------------------

    enum class Family { PETER }

    @Nested
    @DisplayName("Supported Types Tests")
    inner class Types {
        private fun <T> test(value: Any?, rm: RowMapper<T>): T {
            SQLite(config).use { db ->
                db.execute("CREATE TABLE test (value)")

                val sql = "INSERT INTO test (value) VALUES (:value)"
                db.execute(sql, mapOf("value" to value))

                db.query("SELECT value FROM test") { rs ->
                    while (rs.next()) {
                        val s = rs.getString("value")
                        println("${value!!::class.simpleName} -> ${s}")
                    }
                }

                return db.query("SELECT value FROM test", rm)
                    .map { it.getOrThrow() }
                    .firstOrNull()
                    ?: Assertions.fail("No value found")
            }
        }

        @Test
        fun `kotlin enum`() {
            val expect = Family.PETER
            val actual = test(expect) { rs, _ ->
                rs.getEnum("value", Family::class)
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun uuid() {
            val expect = UUID.fromString("12345678-1234-5678-1234-567812345678")
            val actual = test(expect) { rs, _ ->
                rs.getUUID("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun localDateTime() {
            val expect = LocalDateTime.of(1999, 1, 31, 12, 34, 56, 777_777_777)
            val actual = test(expect) { rs, _ ->
                rs.getLocalDateTime("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun localDate() {
            val expect = LocalDate.of(1999, 1, 31)
            val actual = test(expect) { rs, _ ->
                rs.getLocalDate("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun localTime() {
            val expect = LocalTime.of(12, 34, 56, 777_777_777)
            val actual = test(expect) { rs, _ ->
                rs.getLocalTime("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun offsetDateTime() {
            val expect = OffsetDateTime.of(
                1999, 1, 31, 12, 34, 56, 777_777_777,
                ZoneOffset.ofHours(2)
            )
            val actual = test(expect) { rs, _ ->
                rs.getOffsetDateTime("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun offsetTime() {
            val expect = OffsetTime.of(12, 34, 56, 777_777_777, ZoneOffset.ofHours(2))
            val actual = test(expect) { rs, _ ->
                rs.getOffsetTime("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun zonedDateTime() {
            val expect = ZonedDateTime.of(
                1999, 1, 31, 12, 34, 56, 777_777_777,
                ZoneId.of("Europe/Berlin")
            )
            val actual = test(expect) { rs, _ ->
                rs.getZonedDateTime("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun zoneId() {
            val expect = ZoneId.of("Europe/Berlin")
            val actual = test(expect) { rs, _ ->
                rs.getZoneId("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun zoneOffset() {
            val expect = ZoneOffset.ofHours(2)
            val actual = test(expect) { rs, _ ->
                rs.getZoneOffset("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun year() {
            val expect = Year.of(1999)
            val actual = test(expect) { rs, _ ->
                rs.getYear("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun yearMonth() {
            val expect = YearMonth.of(1999, 1)
            val actual = test(expect) { rs, _ ->
                rs.getYearMonth("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun month() {
            val expect = Month.JANUARY
            val actual = test(expect) { rs, _ ->
                rs.getMonth("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun monthDay() {
            val expect = MonthDay.of(Month.JANUARY, 31)
            val actual = test(expect) { rs, _ ->
                rs.getMonthDay("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun dayOfWeek() {
            val expect = DayOfWeek.SUNDAY
            val actual = test(expect) { rs, _ ->
                rs.getDayOfWeek("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun duration() {
            val expect = Duration.ofHours(2)
            val actual = test(expect) { rs, _ ->
                rs.getDuration("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun period() {
            val expect = Period.of(1, 2, 3)
            val actual = test(expect) { rs, _ ->
                rs.getPeriod("value")
            }
            assertThat(actual).isEqualTo(expect)
        }

        @Test
        fun sqlValue() {
            @Serializable
            data class Character(val name: String) : SQLValue<String> {
                override fun sqlValue() = Json.encodeToString(this)
            }

            val expect = Character("Peter")
            val actual: Character = test(expect) { rs, _ ->
                Json.decodeFromString(rs.getString("value"))
            }
            assertThat(actual).isEqualTo(expect)
        }
    }

    // -------------------------------------------------------------------------
    //
    // Parameterized
    //
    // -------------------------------------------------------------------------

    @Test
    fun `With Named Parameters`() {
        SQLite(config).use { db ->
            var sql = """
                    CREATE TABLE test (num);
                    INSERT INTO test (num) VALUES (1), (2), (3), (4), (5), (6), (7);
                    INSERT INTO test (num) VALUES (42), (69), (420), (666), (1337);
                """
            db.execute(sql)

            sql = "SELECT * FROM test WHERE num IN (:numbers)"
            val params = mapOf("numbers" to listOf(42, 69, 420, 666, 1337))

            val res = db.query(sql, params) { rs, _ ->
                rs.getInt("num")
            }.getOrThrow()
            assertThat(res).containsExactly(42, 69, 420, 666, 1337)
        }
    }

    // -------------------------------------------------------------------------
    //
    // SqlCollections
    //
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("SqlCollection Queries")
    inner class SqlCollection {
        val table = """
            CREATE TABLE equipment (weapon TEXT, armor TEXT, faction TEXT);
            INSERT INTO equipment (weapon, armor, faction)
            VALUES ('Longsword',   'Plate Armor', 'Knights of the Round Table'),
                   ('Morningstar', 'Scale Armor',      'Knights of the Round Table'),
                   ('Glaive',      'Dragonhide Armor', 'Knights of the Round Table'),
                   ('Magic Staff', 'Enchanted Cloak',  'Knights of the Round Table'),
                   ('Flail',       'Iron Plate',       'Knights of the Round Table'),
                   
                   ('Longsword',   'Plate Armor',      'Dwarven Guards'),
                   ('Morningstar', 'Scale Armor',      'Dwarven Guards'),
                   ('Glaive',      'Dragonhide Armor', 'Dwarven Guards'),
                   ('Magic Staff', 'Enchanted Cloak',  'Dwarven Guards'),
                   ('Flail',       'Iron Plate',       'Dwarven Guards'),
                   
                   ('Longsword',   'Plate Armor',      'Mystic Assassins'),
                   ('Morningstar', 'Scale Armor',      'Mystic Assassins'),
                   ('Glaive',      'Dragonhide Armor', 'Mystic Assassins'),
                   ('Magic Staff', 'Enchanted Cloak',  'Mystic Assassins'),
                   ('Flail',       'Iron Plate',       'Mystic Assassins'),
                   
                   ('Longsword',   'Plate Armor',      'Vikings'),
                   ('Morningstar', 'Scale Armor',      'Vikings'),
                   ('Glaive',      'Dragonhide Armor', 'Vikings'),
                   ('Magic Staff', 'Enchanted Cloak',  'Vikings'),
                   ('Flail',       'Iron Plate',       'Vikings'),
                   
                   ('Longsword',   'Plate Armor',      'Dragon Knights'),
                   ('Morningstar', 'Scale Armor',      'Dragon Knights'),
                   ('Glaive',      'Dragonhide Armor', 'Dragon Knights'),
                   ('Magic Staff', 'Enchanted Cloak',  'Dragon Knights'),
                   ('Flail',       'Iron Plate',       'Dragon Knights'),
                   
                   ('Longsword',   'Plate Armor',      'Wizard Conclave'),
                   ('Morningstar', 'Scale Armor',      'Wizard Conclave'),
                   ('Glaive',      'Dragonhide Armor', 'Wizard Conclave'),
                   ('Magic Staff', 'Enchanted Cloak',  'Wizard Conclave'),
                   ('Flail',       'Iron Plate',       'Wizard Conclave'),
                   
                   ('Longsword',   'Plate Armor',      'Crusaders'),
                   ('Morningstar', 'Scale Armor',      'Crusaders'),
                   ('Glaive',      'Dragonhide Armor', 'Crusaders'),
                   ('Magic Staff', 'Enchanted Cloak',  'Crusaders'),
                   ('Flail',       'Iron Plate',       'Crusaders'),
                   
                   ('Scepter',     'Flowing Robes',    'High Priests'),
                   ('Longsword',   'Plate Armor',      'High Priests'),
                   ('Morningstar', 'Scale Armor',      'High Priests'),
                   ('Glaive',      'Dragonhide Armor', 'High Priests'),
                   ('Magic Staff', 'Enchanted Cloak',  'High Priests'),
                   ('Flail',       'Iron Plate',       'High Priests');
            """

        @Test
        fun `With Named Parameters`() {
            val sql = """
                SELECT * FROM equipment
                WHERE (weapon, armor) IN (:equipment)
                  AND (faction IN (:my_factions) OR faction IN (:your_factions))
            """
            val wantedEquipment = SQLList(
                SQLPair("Longsword", "Plate Armor"),
                SQLPair("Morningstar", "Scale Armor"),
            )
            val myFactions = listOf(
                "Knights of the Round Table",
                "Crusaders",
                "Vikings",
                "High Priests",
            )
            val yourFactions = listOf(
                "Mystic Assassins",
                "Dwarven Guards",
                "Dragon Knights",
                "Wizard Conclave",
            )
            val params = mapOf(
                "equipment" to wantedEquipment,
                "my_factions" to myFactions,
                "your_factions" to yourFactions
            )

            val res = SQLite(config).use { db ->
                db.execute(table)
                db.query(sql, params) { rs, _ ->
                    Triple(
                        rs.getString("weapon"),
                        rs.getString("armor"),
                        rs.getString("faction")
                    )
                }.getOrThrow()
            }

            val factions = myFactions + yourFactions
            assertThat(res).hasSize(factions.size * 2)
            val expect = factions.flatMap {
                listOf(
                    Triple("Longsword", "Plate Armor", it),
                    Triple("Morningstar", "Scale Armor", it)
                )
            }
            assertThat(res).containsExactlyInAnyOrder(*expect.toTypedArray())
        }
    }

    // -------------------------------------------------------------------------
    //
    // Batch
    //
    // -------------------------------------------------------------------------

    @Test
    fun `Batch Update`() {
        SQLite(config).use { db ->
            db.execute("CREATE TABLE test (num)")

            val sql = "INSERT INTO test (num) VALUES (:num)"
            val batches = buildList {
                add(mapOf("num" to 2))
                add(mapOf("num" to 4))
                add(mapOf("num" to 6))
                add(mapOf("num" to 8))
            }

            val res = db.batchUpdate(sql, batches)
            assertThat(res).containsExactly(1, 1, 1, 1)
            val nums = db.query("SELECT * FROM test") { rs, _ ->
                rs.getInt("num")
            }.map { it.getOrThrow() }
            assertThat(nums).containsExactly(2, 4, 6, 8)
        }
    }

    // -------------------------------------------------------------------------
    //
    // Transactions
    //
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Transaction Management")
    inner class TransactionManagement {
        val table = """
            CREATE TABLE Accounts (
                id INTEGER PRIMARY KEY,
                balance NUMERIC(10, 2)
            );

            INSERT INTO Accounts (id, balance) VALUES (1, 5000.00);
            INSERT INTO Accounts (id, balance) VALUES (2, 3000.00);
        """

        @Test
        fun `Rollback Behaviour`() {
            SQLite(config).use { db ->
                db.execute(table)

                try {
                    // Much of this code could be removed, but it's here just
                    // for demonstration purposes.
                    db.transaction {
                        val amount = 8000

                        // Withdraw from Account 1
                        var sql = """
                            UPDATE Accounts
                            SET balance = balance - $amount
                            WHERE id = 1
                        """
                        val n = db.execute(sql)
                        assertThat(n).isEqualTo(1)

                        // Check balance
                        sql = "SELECT balance FROM Accounts WHERE id = 1"
                        val balance = db.query(sql) { rs ->
                            rs.next()
                            rs.getBigDecimal("balance")
                        }
                        if (balance < BigDecimal.ZERO)
                            error("Insufficient balance")

                        // Deposit to Account 2 - should never run
                        sql = """
                            UPDATE Accounts
                            SET balance = balance + $amount
                            WHERE id = 2
                        """
                        db.execute(sql)
                    }
                } catch (e: IllegalStateException) {
                    println("Transaction failed, as expected.")
                }

                val sql = "SELECT balance FROM Accounts WHERE id = 1"
                val balance = db.query(sql) { rs ->
                    rs.next()
                    rs.getBigDecimal("balance")
                }
                assertThat(balance).isEqualTo(BigDecimal("5000"))
            }
        }

        @Test
        fun `Nested Transactions`() {
            SQLite(config).use { db ->
                db.execute(table)

                try {
                    db.transaction {
                        val amount = 8000
                        db.transaction {
                            // Withdraw from Account 1
                            val sql = """
                                UPDATE Accounts
                                SET balance = balance - $amount
                                WHERE id = 1
                            """
                            val n = db.execute(sql)
                            assertThat(n).isEqualTo(1)
                            // The transaction should not be committed here,
                            // that's the point of this test.
                        }
                        error("Fake error.")
                    }
                } catch (e: IllegalStateException) {
                    println("Transaction failed, as expected.")
                }

                val sql = "SELECT balance FROM Accounts WHERE id = 1"
                val balance = db.query(sql) { rs ->
                    rs.next()
                    rs.getBigDecimal("balance")
                }
                assertThat(balance).isEqualTo(BigDecimal("5000"))
            }
        }
    }

    // -------------------------------------------------------------------------
    //
    // Concurrency
    //
    // -------------------------------------------------------------------------

    @Test
    fun `Multithreading isn't allowed`() {
        val table = """
                CREATE TABLE test (num);
                INSERT INTO test (num) VALUES (1), (2), (3), (4), (5), (6), (7);
                INSERT INTO test (num) VALUES (42), (69), (420), (666), (1337);
            """

        SQLite(config).use { db ->
            db.execute(table)

            val sql = "SELECT * FROM test"
            // Calling from the owner thread is OK
            db.execute(sql)

            // Calling from another thread is not OK
            assertThrows<SQLiteException> {
                runBlocking(Dispatchers.Default) {
                    launch {
                        db.execute(sql)
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    //
    // Foreign Keys
    //
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Foreign Keys")
    inner class ForeignKeys {
        val table = """
            CREATE TABLE foo (id);
            CREATE TABLE bar (fk REFERENCES foo (id));
            
            INSERT INTO foo (id) VALUES (1), (2);
            INSERT INTO bar (fk) VALUES (1), (2), (3), (4);
        """.trimIndent()

        @Test
        fun `Foreign Key Constraints`() {
            // Without foreign key constraints, this should work
            SQLite(config).use { db ->
                db.execute(table)
            }

            // With foreign key constraints, this should fail
            val config = config.copy(foreignKeys = true)
            assertThrows<SQLiteException> {
                SQLite(config).use { db ->
                    db.execute(table)
                }
            }
        }

        @Test
        fun `Foreign Key Check`() {
            SQLite(config).use { db ->
                db.execute(table)

                assertThrows<SQLiteException> {
                    db.checkForeignKeys()
                }
            }
        }
    }
}
