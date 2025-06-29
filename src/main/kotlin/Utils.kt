package dev.hirth.sqlite

import kotlinx.serialization.json.Json
import java.sql.ResultSet
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Unwrap all the results in the list. Throws an exception if any of the
 * results are a failure, otherwise returns a list of the unwrapped results.
 *
 * Throws the first encountered exception, with all other exceptions
 * added as suppressed exceptions.
 *
 * @throws Throwable
 */
fun <T> List<Result<T>>.getOrThrow(): List<T> {
    val res = mutableListOf<T>()
    var exc: Throwable? = null

    for (item in this) {
        if (item.isFailure) {
            if (exc == null)
                exc = item.exceptionOrNull()!!
            else
                exc.addSuppressed(item.exceptionOrNull()!!)
        } else {
            res.add(item.getOrThrow())
        }
    }

    if (exc != null) throw exc
    return res
}

/**
 * Unwrap the result of the list, expecting a single result.
 *
 * Throws an exception if it encounters any failure, or if the list
 * is empty or contains more than one result.
 *
 * @see single
 * @see getOrThrow
 */
fun <T> List<Result<T>>.getSingleOrThrow(): T {
    return getOrThrow().single()
}

/**
 * Unwrap the result of the list, expecting a single result.
 * Returns `null` if the list is empty, or if it contains more than one result.
 *
 * Throws an exception if it encounters any failures in the list.
 *
 * @see singleOrNull
 * @see getOrThrow
 */
fun <T> List<Result<T>>.getSingleOrNull(): T? {
    return getOrThrow().singleOrNull()
}

/**
 * Get an enum from the result set. [Enum.name] is used to match the enum
 * constant.
 *
 * @throws NoSuchElementException if the enum constant is not found.
 */
fun <T : Enum<T>> ResultSet.getEnum(column: String, clazz: Class<T>): T? {
    val str = getString(column) ?: return null
    return clazz.enumConstants.first { it.name == str }
}

/**
 * Get an enum from the result set. [Enum.name] is used to match the enum
 * constant.
 *
 * @throws NoSuchElementException if the enum constant is not found.
 */
fun <T : Enum<T>> ResultSet.getEnum(column: String, clazz: KClass<T>): T? =
    getEnum(column, clazz.java)

/**
 * Get an integer from the result set (with better null-handling than
 * [ResultSet.getInt]).
 *
 * @throws NumberFormatException if the column value is not a valid integer.
 */
fun ResultSet.getInteger(column: String) = getString(column)?.toInt()

/**
 * Get a JSON object from the result set, decoded with the provided [Json] instance.
 */
inline fun <reified T> ResultSet.getJson(column: String, json: Json = Json): T? {
    val str = getString(column) ?: return null
    return json.decodeFromString<T>(str)
}

/**
 * Get a value from the result set, converted from a string representation.
 */
inline fun <T> ResultSet.get(column: String, convert: (String) -> T): T? {
    val str = getString(column) ?: return null
    return convert(str)
}

// -----------------------------------------------------------------------------
//
// Kotlin types
//
// -----------------------------------------------------------------------------

/**
 * Retrieves a [Uuid] value from the specified column in the `ResultSet`.
 * This does not require the database driver to support Uuid.
 *
 * Will fetch the bytes from the database and determine if the uuid
 * is stored as bytes or string.
 *
 * @param column The name of the column containing the UUID value.
 * @return The [Uuid] object if the column data is valid, or `null` if the column is `NULL`.
 * @throws IllegalArgumentException if the column data cannot be interpreted as a valid UUID.
 */
@OptIn(ExperimentalUuidApi::class)
fun ResultSet.getUuid(column: String): Uuid? {
    val bytes = getBytes(column) ?: return null

    return when (bytes.size) {
        16 -> Uuid.fromByteArray(bytes)
        // String format: 11111111-2222-4444-aaaa-ffffffffffff
        36 -> Uuid.parse(bytes.decodeToString())
        else -> {
            val str = bytes.decodeToString()
            val msg = "$column: Invalid UUID: $str (${bytes.size} bytes)"
            throw IllegalArgumentException(msg)
        }
    }
}

// -----------------------------------------------------------------------------
//
// Java types
//
// -----------------------------------------------------------------------------

/**
 * Get a [UUID] object from the result set. This does not require the database
 * driver to support UUIDs.
 *
 * Will fetch the bytes from the database and determine if the uuid
 * is stored as bytes or string.
 *
 * @param column The name of the column containing the UUID value.
 * @return The [UUID] object if the column data is valid, or `null` if the column is `NULL`.
 * @throws IllegalArgumentException if the column data cannot be interpreted as a valid UUID.
 */
fun ResultSet.getUUID(column: String): UUID? {
    val bytes = getBytes(column) ?: return null

    return when (bytes.size) {
        16 -> {
            val msb = bytes.toLongAt(0)
            val lsb = bytes.toLongAt(8)
            UUID(msb, lsb)
        }

        // String format: 11111111-2222-4444-aaaa-ffffffffffff
        36 -> UUID.fromString(bytes.decodeToString())

        else -> {
            val str = bytes.decodeToString()
            val msg = "$column: Invalid UUID: $str (${bytes.size} bytes)"
            throw IllegalArgumentException(msg)
        }
    }
}

private fun ByteArray.toLongAt(pos: Int): Long {
    var value = 0L
    for (i in pos until pos + 8) {
        val byte = this[i].toLong() and 0xff
        val mask = byte shl (7 - i + pos) * 8
        value = value or mask
    }
    return value
}

/**
 * Get a [LocalDateTime] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see LocalDateTime.parse for more information on the format.
 */
fun ResultSet.getLocalDateTime(
    column: String,
    formatter: DateTimeFormatter? = null
): LocalDateTime? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        LocalDateTime.parse(str, formatter)
    } else {
        LocalDateTime.parse(str)
    }
}

/**
 * Get a [LocalDate] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see LocalDate.parse for more information on the format.
 */
fun ResultSet.getLocalDate(
    column: String,
    formatter: DateTimeFormatter? = null
): LocalDate? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        LocalDate.parse(str, formatter)
    } else {
        LocalDate.parse(str)
    }
}

/**
 * Get a [LocalTime] from the result set. The time is parsed using the
 * [formatter] provided.
 *
 * @see LocalTime.parse for more information on the format.
 */
fun ResultSet.getLocalTime(
    column: String,
    formatter: DateTimeFormatter? = null
): LocalTime? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        LocalTime.parse(str, formatter)
    } else {
        LocalTime.parse(str)
    }
}

/**
 * Get an [OffsetDateTime] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see OffsetDateTime.parse for more information on the format.
 */
fun ResultSet.getOffsetDateTime(
    column: String,
    formatter: DateTimeFormatter? = null
): OffsetDateTime? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        OffsetDateTime.parse(str, formatter)
    } else {
        OffsetDateTime.parse(str)
    }
}

/**
 * Get an [OffsetTime] from the result set. The time is parsed using the
 * [formatter] provided.
 *
 * @see OffsetTime.parse for more information on the format.
 */
fun ResultSet.getOffsetTime(
    column: String,
    formatter: DateTimeFormatter? = null
): OffsetTime? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        OffsetTime.parse(str, formatter)
    } else {
        OffsetTime.parse(str)
    }
}

/**
 * Get a [ZonedDateTime] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see ZonedDateTime.parse for more information on the format.
 */
fun ResultSet.getZonedDateTime(
    column: String,
    formatter: DateTimeFormatter? = null
): ZonedDateTime? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        ZonedDateTime.parse(str, formatter)
    } else {
        ZonedDateTime.parse(str)
    }
}

/**
 * Get a [ZoneId] from the result set. The zone ID is parsed using the
 * [formatter] provided.
 *
 * @see ZoneId.of for more information on the format.
 */
fun ResultSet.getZoneId(column: String): ZoneId? {
    val str = getString(column) ?: return null
    return ZoneId.of(str)
}

/**
 * Get a [ZoneOffset] from the result set. The zone offset is parsed using the
 * [formatter] provided.
 *
 * @see ZoneOffset.of for more information on the format.
 */
fun ResultSet.getZoneOffset(column: String): ZoneOffset? {
    val str = getString(column) ?: return null
    return ZoneOffset.of(str)
}

/**
 * Get a [Year] from the result set. The year is parsed using the
 * [formatter] provided.
 *
 * @see Year.parse for more information on the format.
 */
fun ResultSet.getYear(
    column: String,
    formatter: DateTimeFormatter? = null
): Year? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        Year.parse(str, formatter)
    } else {
        Year.parse(str)
    }
}

/**
 * Get a [YearMonth] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see YearMonth.parse for more information on the format.
 */
fun ResultSet.getYearMonth(
    column: String,
    formatter: DateTimeFormatter? = null
): YearMonth? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        YearMonth.parse(str, formatter)
    } else {
        YearMonth.parse(str)
    }
}

/**
 * Get a [Month] from the result set.
 *
 * @see Month.valueOf for more information on the format.
 */
fun ResultSet.getMonth(column: String): Month? {
    val str = getString(column) ?: return null
    return Month.valueOf(str)
}

/**
 * Get a [MonthDay] from the result set. The date is parsed using the
 * [formatter] provided.
 *
 * @see MonthDay.parse for more information on the format.
 */
fun ResultSet.getMonthDay(
    column: String,
    formatter: DateTimeFormatter? = null
): MonthDay? {
    val str = getString(column) ?: return null
    return if (formatter != null) {
        MonthDay.parse(str, formatter)
    } else {
        MonthDay.parse(str)
    }
}

/**
 * Get a [DayOfWeek] from the result set.
 *
 * @see DayOfWeek.valueOf for more information on the format.
 */
fun ResultSet.getDayOfWeek(column: String): DayOfWeek? {
    val str = getString(column) ?: return null
    return DayOfWeek.valueOf(str)
}

/**
 * Get a [Duration] from the result set.
 *
 * @see Duration.parse for more information on the format.
 */
fun ResultSet.getDuration(column: String): Duration? {
    val str = getString(column) ?: return null
    return Duration.parse(str)
}

/**
 * Get a [Period] from the result set.
 *
 * @see Period.parse for more information on the format.
 */
fun ResultSet.getPeriod(column: String): Period? {
    val str = getString(column) ?: return null
    return Period.parse(str)
}

/**
 * Get an [Instant] from the result set.
 *
 * @see Instant.parse
 */
fun ResultSet.getInstant(column: String): Instant? {
    val str = getString(column) ?: return null
    return Instant.parse(str)
}
