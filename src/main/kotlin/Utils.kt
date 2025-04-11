package games.soloscribe.sqlite

import java.sql.ResultSet
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

// -----------------------------------------------------------------------------
//
// Kotlin types
//
// -----------------------------------------------------------------------------

@OptIn(ExperimentalUuidApi::class)
fun ResultSet.getUuid(column: String): Uuid? {
    val str = getString(column) ?: return null
    return Uuid.parse(str)
}

// -----------------------------------------------------------------------------
//
// Java types
//
// -----------------------------------------------------------------------------

/**
 * Get a [UUID] object from the result set. This does not require the database
 * driver to support UUIDs, and works with any database.
 */
fun ResultSet.getUUID(column: String): UUID? {
    val str = getString(column) ?: return null
    return UUID.fromString(str)
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
