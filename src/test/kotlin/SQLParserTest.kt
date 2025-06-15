package dev.hirth.sqlite

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SQLParserTest {

    // -------------------------------------------------------------------------
    // Splitting

    @ParameterizedTest
    @MethodSource("splitTestData")
    @DisplayName("Split Statements")
    fun testSplit(td: SplitTestData) {
        val actual = SQLParser(td.sql).parse()
        assertThat(actual).hasSize(td.expected.size)

        for (i in actual.indices) {
            val sql = actual[i].resolveSQL()
            assertThat(sql)
                .describedAs("Statement %d", i)
                .isEqualTo(td.expected[i])
        }
    }

    data class SplitTestData(val sql: String, val expected: Array<String>)

    // -------------------------------------------------------------------------
    // Named parameters

    @ParameterizedTest
    @MethodSource("namedTestData")
    @DisplayName("Expand Named Parameters")
    fun testNamed(td: NamedTestData) {
        val parser = SQLParser(td.sql)
        parser.shouldSplit = false
        val actual = parser.parse()
        assertThat(actual).hasSize(1)

        val stmt = actual[0]
        assertThat(stmt.resolveSQL(td.params))
            .describedAs("Expanded SQL")
            .isEqualTo(td.expectedSQL)
        assertThat(stmt.resolveParams(td.params))
            .describedAs("Expanded parameters")
            .containsExactly(*td.expectedParams)
    }

    @JvmRecord
    data class NamedTestData(
        val sql: String,
        val expectedSQL: String,
        val params: Map<String, Any>,
        val expectedParams: Array<Any?>
    )

    companion object {
        @JvmStatic
        fun splitTestData(): Stream<SplitTestData> {
            return Stream.of(

                // Single, simple statements

                SplitTestData(
                    "SELECT * FROM foo",
                    arrayOf("SELECT * FROM foo")
                ),
                SplitTestData(
                    "SELECT * FROM foo;",
                    arrayOf("SELECT * FROM foo;")
                ),

                // Multiple statements

                SplitTestData(
                    "SELECT * FROM foo; SELECT * FROM bar;",
                    arrayOf("SELECT * FROM foo;", " SELECT * FROM bar;")
                ),

                // String with semicolon

                SplitTestData(
                    "SELECT * FROM foo; SELECT 'hel;lo' FROM bar",
                    arrayOf("SELECT * FROM foo;", " SELECT 'hel;lo' FROM bar")
                ),

                // String with escaped quote

                SplitTestData(
                    "SELECT 'hel\\'lo' FROM bar; SELECT 1",
                    arrayOf("SELECT 'hel\\'lo' FROM bar;", " SELECT 1")
                ),

                // SQL comment

                SplitTestData(
                    "-- SELECT 1;\nSELECT 42",
                    arrayOf("-- SELECT 1;\nSELECT 42")
                ),

                // C comment

                SplitTestData(
                    "/* SELECT 1; */ SELECT 42",
                    arrayOf("/* SELECT 1; */ SELECT 42")
                ),

                // CPP comment

                SplitTestData(
                    "// SELECT 1;\nSELECT 42",
                    arrayOf("// SELECT 1;\nSELECT 42")
                ),

                // BEGIN/END block

                SplitTestData(
                    "BEGIN SELECT 1; SELECT 2; END",
                    arrayOf("BEGIN SELECT 1; SELECT 2; END")
                ),
                SplitTestData(
                    "BEGIN SELECT 1; END; BEGIN SELECT 2; END",
                    arrayOf("BEGIN SELECT 1; END;", " BEGIN SELECT 2; END")
                ),
            )
        }

        @JvmStatic
        fun namedTestData(): Stream<NamedTestData> {
            return Stream.of( // Simple case
                NamedTestData(
                    "SELECT * FROM foo WHERE id = :id AND name = :name",
                    "SELECT * FROM foo WHERE id = ? AND name = ?",
                    mapOf("id" to 42, "name" to "bar"),
                    arrayOf(42, "bar")
                ),

                // Multiple occurrences of same parameter

                NamedTestData(
                    "SELECT * FROM foo WHERE id = :id AND name = :id",
                    "SELECT * FROM foo WHERE id = ? AND name = ?",
                    mapOf("id" to 42),
                    arrayOf(42, 42)
                ),

                // Reverse order

                NamedTestData(
                    "SELECT * FROM foo WHERE name = :name AND id = :id",
                    "SELECT * FROM foo WHERE name = ? AND id = ?",
                    mapOf("id" to 42, "name" to "bar"),
                    arrayOf("bar", 42)
                ),

                // Comments

                NamedTestData(
                    "SELECT * FROM foo WHERE id = :id -- AND name = :name\n",
                    "SELECT * FROM foo WHERE id = ? -- AND name = :name\n",
                    mapOf("id" to 42),
                    arrayOf(42)
                ),
                NamedTestData(
                    "// :id\nSELECT * FROM foo WHERE id = :id",
                    "// :id\nSELECT * FROM foo WHERE id = ?",
                    mapOf("id" to 42),
                    arrayOf(42)
                ),
                NamedTestData(
                    "SELECT * FROM foo WHERE /* name = :name AND */ id = :id",
                    "SELECT * FROM foo WHERE /* name = :name AND */ id = ?",
                    mapOf("id" to 42, "name" to "bar"),
                    arrayOf(42)
                ),

                // List parameters

                NamedTestData(
                    "SELECT :columns FROM foo WHERE id IN (:ids)",
                    "SELECT ?,?,? FROM foo WHERE id IN (?,?)",
                    mapOf(
                        "ids" to listOf(42, 43),
                        "columns" to listOf("id", "name", "age")
                    ),
                    arrayOf("id", "name", "age", 42, 43)
                ),

                // SQLCollection parameters

                NamedTestData(
                    "SELECT * FROM foo WHERE (weapon, shield) IN (:equipment)",
                    "SELECT * FROM foo WHERE (weapon, shield) IN ((?,?),(?,?))",
                    mapOf(
                        "equipment" to SQLList(
                            SQLPair("sword", "buckler"),
                            SQLPair("axe", "tower")
                        )
                    ),
                    arrayOf("sword", "buckler", "axe", "tower")
                )
            )
        }
    }
}