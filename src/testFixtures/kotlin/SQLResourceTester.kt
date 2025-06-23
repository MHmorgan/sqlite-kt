package dev.hirth.sqlite

import org.assertj.core.api.Assertions.assertThat

/**
 * A utility class for testing instances of [SQLResource].
 *
 * @param I The type of the identifier for the resource.
 * @param T The type of the resource.
 * @property testObjects A list of test objects of type [T] to use for testing.
 * @property identifierOf A function to extract the identifier from a test object.
 */
class SQLResourceTester<I, T>(
    private val testObjects: List<T>,
    private val identifierOf: (T) -> I
) {

    /**
     * Tests all functionality of a [SQLResource] implementation using the provided test objects.
     *
     * This method performs comprehensive testing of:
     * - Basic retrieval operations (invoke, get, contains)
     * - Insert operations (insert, plusAssign)
     * - Delete operations (delete, minusAssign)
     * - Edge cases and complex scenarios
     *
     * @param resource The [SQLResource] instance to test.
     */
    fun test(resource: SQLResource<I, T>) {
        require(testObjects.isNotEmpty()) { "Test objects list cannot be empty" }

        val firstObj = testObjects.first()
        val firstId = identifierOf(firstObj)

        // -------------------------------------
        // Test on empty resources
        // -------------------------------------

        assertThat(resource()).doesNotContainAnyElementsOf(testObjects)
        assertThat(resource[firstId]).isNull()
        assertThat(firstId in resource).isFalse()
        assertThat(resource.delete(firstId)).isEqualTo(0)

        // -------------------------------------
        // Test insert
        // -------------------------------------

        // Test insert method
        run {
            val n = resource.insert(firstObj)
            assertThat(n).isGreaterThanOrEqualTo(1)
            assertThat(resource[firstId]).isEqualTo(firstObj)
        }

        // Test plusAssign operator with remaining objects
        for (obj in testObjects.drop(1)) {
            resource += obj
            val id = identifierOf(obj)
            assertThat(resource[id]).isEqualTo(obj)
        }

        // -------------------------------------
        // Test retrieval
        // -------------------------------------

        // Test invoke() - get all items
        run {
            val all = resource()
            assertThat(all).hasSizeGreaterThanOrEqualTo(testObjects.size)
            assertThat(all).containsAll(testObjects)
        }

        // Test get() and contains() for each object
        for (obj in testObjects) {
            val id = identifierOf(obj)
            val actual = resource[id]
            assertThat(actual).isEqualTo(obj)
            assertThat(id in resource).isTrue()
        }

        // -------------------------------------
        // Test deleting
        // -------------------------------------

        // Test delete method
        run {
            val n = resource.delete(firstId)
            assertThat(n).isEqualTo(1)
            assertThat(resource[firstId]).isNull()
            assertThat(firstId in resource).isFalse()
        }

        // Test minusAssign operator if we have more objects
        for ((i, obj) in testObjects.withIndex().drop(1)) {
            val all = resource()
            assertThat(all).containsAll(testObjects.drop(i))
            assertThat(all).doesNotContainAnyElementsOf(testObjects.take(i))

            val id = identifierOf(obj)
            resource -= id
            assertThat(resource[id]).isNull()
            assertThat(id in resource).isFalse()
        }
    }
}
