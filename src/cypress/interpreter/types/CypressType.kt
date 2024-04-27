package cypress.interpreter.types

interface CypressType<T> {
    val value: T

    fun compareTo(other: CypressType<T>) : CypressInt

    fun equals(other: CypressType<T>) : CypressInt {
        return CypressInt(compareTo(other).value == 0)
    }

    fun asBoolean() : CypressInt
}