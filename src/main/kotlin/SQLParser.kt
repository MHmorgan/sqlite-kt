typealias ParsedSQL = List<ParsedStatement>

fun String.parse(): ParsedSQL = SQLParser(this).parse()

/**
 * A parsed statement is the result of parsing. This contains the
 * information needed to produce the final query string and parameter list.
 *
 * ### Resolving
 *
 * Providing resolving functionality here might feel cumbersome,
 * but it is actually quite convenient. It allows us to use the same
 * `SQLParser` for parsing normal and batch statements, and it allows
 * caching of the parsed statements.
 *
 * The resolving handles List parameters, but no other collection
 * types. While restrictive, it is necessary since ordering is important.
 * An unpredictable ordering of parameters could lead to subtle and
 * hard-to-debug errors.
 */
class ParsedStatement(
    private val tokens: List<Token>,
    private val sql: String,
) {
    /**
     * Resolve the SQL and parameters for this statement based on the given
     * [params] map.
     */
    fun resolve(params: SQLParams): Pair<String, Array<out Any?>> {
        return if (params.isEmpty())
            resolveSQL() to emptyArray()
        else
            resolveSQL(params) to resolveParams(params)
    }

    /**
     * Resolve the SQL if this statement without replacing parameters.
     */
    fun resolveSQL() = tokens.joinToString("") { it.content }

    /**
     * Resolve the SQL of this statement with raw parameters (?) based on
     * the given [params] map.
     */
    fun resolveSQL(params: SQLParams): String {
        return tokens.joinToString("") {
            if (it.type == TokenType.CODE)
                return@joinToString it.content
            if (it.content !in params) {
                val msg = "SQL parameter not found: ${it.content}\n$sql\n"
                throw SQLiteException(msg)
            }
            when (val p = params[it.content]) {
                is SQLCollection -> p.resolveSql()
                is List<*> -> p.joinToString(",") { "?" }
                // I prefer a permissive approach here. I'd rather not try to guess
                // which types are allowed and which are not, because that would
                // restrict the flexibility we have for doing conversion magic in the
                // database driver (if this kind of dark magic ever is deemed necessary).
                else -> "?"
            }
        }
    }

    /**
     * Resolve the parameter array for this statement. The array is resolved
     * based on the given [params] map, to match the raw parameters (?)
     * in the SQL.
     */
    fun resolveParams(params: SQLParams): Array<out Any?> {
        return tokens
            .filter { it.type == TokenType.PARAM }
            .flatMap {
                if (it.content !in params) {
                    val msg = "SQL parameter not found: ${it.content}\n$sql\n"
                    throw SQLiteException(msg)
                }
                when (val p = params[it.content]) {
                    is SQLCollection -> p.resolveParams()
                    is List<*> -> p
                    else -> listOf(p)
                }
            }
            .toTypedArray()
    }
}

/**
 * A token of a parsed sql statement.
 */
data class Token(
    val type: TokenType,
    val content: String,
    val line: Int,
    val col: Int,
)

enum class TokenType {
    CODE,
    PARAM
}

/**
 * [SQLParser] provides all the functionality needed which requires some
 * knowledge of SQL syntax. This includes splitting statements, and parameter
 * replacement. A parser may only be used once.
 *
 * It is not meant to be a general purpose SQL parser. It is only meant
 * to provide the specific functionality needed, for the SQLite syntax.
 *
 * @property sql The SQL to parse.
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class SQLParser(val sql: String) {

    /**
     * Enable splitting of statements during parsing. Splitting is done on
     * semicolon characters, and is enabled by default.
     */
    var shouldSplit: Boolean = true

    var tokens = mutableListOf<Token>()
    var statements = mutableListOf<ParsedStatement>()

    var pos = 0
    var line = 1
    var col = 1
    var start = 0
    var startStmt = 0
    var startLine = 1
    var startCol = 1

    var state = State.NORMAL

    var delimiter: String = ""

    /**
     * Run the parser and return a list of parsed statements.
     */
    fun parse(): ParsedSQL {
        // Parse the SQL by running the FSM until we reach the end.
        while (pos < sql.length) {
            when (state) {
                State.NORMAL -> normal()
                State.PARAM -> named()
                else -> delimited()
            }
        }
        when (state) {
            State.NORMAL, State.COMMENT -> {
                if (start < sql.length)
                    addCodeToken(sql.substring(start))
            }

            State.PARAM -> named()
            State.STRING -> throw InvalidSQL("Unterminated string")
        }
        consumeStatement()
        return statements
    }

    // -------------------------------------------------------------------------
    // STATE MACHINE

    enum class State {
        NORMAL,
        PARAM,

        // Delimited
        STRING,
        COMMENT
    }

    private fun normal() {
        when (next()) {
            ':' -> {
                state = when {
                    isIdentifier(peek()) -> State.PARAM
                    peek() == ':' -> {
                        next() // Skip the second colon
                        return
                    }

                    else -> return
                }
                // -1 because we don't want to include the colon
                addCodeToken(sql.substring(start, pos - 1))
                updateStart()
            }

            ';' -> {
                if (!shouldSplit) return
                addCodeToken(sql.substring(start, pos))
                consumeStatement()
                updateStart()
            }

            '\'', '"' -> {
                delimiter = peek(0).toString()
                state = State.STRING
            }

            '-' -> {
                if (peek() == '-') {
                    delimiter = "\n"
                    state = State.COMMENT
                    next()
                }
            }

            '/' -> {
                delimiter = when (peek()) {
                    '*' -> "*/"
                    '/' -> "\n"
                    else -> return
                }
                state = State.COMMENT
                next()
            }
        }
    }

    private fun named() {
        // Consume identifier characters until we hit a non-identifier char.
        if (isIdentifier(peek())) {
            next()
            return
        }
        addParamToken(sql.substring(start, pos))
        updateStart()
        state = State.NORMAL
    }

    private fun delimited() {
        // Skip escaped characters
        if (peek() == '\\') {
            next(2)
            return
        }
        // Check if the delimiter match current position
        for (i in delimiter.indices) { // This doesn't do utf-8 properly
            if (peek(i + 1) != delimiter[i]) {
                next()
                return
            }
        }
        next(delimiter.length)
        state = State.NORMAL
        delimiter = ""
    }

    private fun peek(n: Int): Char {
        val peekPos = pos + n - 1 // -1 because we're already at the next char
        return if (peekPos >= sql.length) 0.toChar() else sql[peekPos]
    }

    private fun peek(): Char = peek(1)

    fun next(): Char {
        if (pos >= sql.length) return 0.toChar()
        val c = sql[pos++]
        if (c == '\n') {
            line++
            col = 1
        } else {
            col++
        }
        return c
    }

    fun next(n: Int) {
        for (i in 1..n)
            next()
    }

    // -------------------------------------------------------------------------
    // HELPERS

    // For some reason 0 is a valid identifier char, which we don't want...
    private fun isIdentifier(c: Char) = c.isJavaIdentifierPart() && c != 0.toChar()

    private fun updateStart() {
        start = pos
        startLine = line
        startCol = col
    }

    private fun consumeStatement() {
        if (tokens.isNotEmpty()) {
            val s = sql.substring(startStmt, pos)
            if (s.isNotBlank())
                statements.add(ParsedStatement(tokens, s))
        }
        startStmt = pos
        tokens = mutableListOf()
    }

    private fun addCodeToken(content: String) {
        tokens.add(Token(TokenType.CODE, content, startLine, startCol))
    }

    private fun addParamToken(content: String) {
        tokens.add(Token(TokenType.PARAM, content, startLine, startCol))
    }

    /**
     * Exception thrown by [SQLParser] when an invalid SQL is encountered.
     */
    inner class InvalidSQL(msg: String) : SQLiteException("sql:$line:$col: $msg")
}