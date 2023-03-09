package cypress.interpreter.types

import cypress.CypressError

class CypressInt(final override val value: Int) : CypressNumber {
    constructor(value: String) : this(value.toInt())

    operator fun plus(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(value + otherValue)
            is Double -> CypressDouble(value + otherValue)
            else -> throw CypressError.CypressTypeError("Unable to add ${otherValue::class.simpleName} to Int.")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}