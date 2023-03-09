package cypress.interpreter.types

import cypress.CypressError

open class CypressDouble(override val value: Double) : CypressNumber {
    constructor(value: String) : this(value.toDouble())

    operator fun plus(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value + otherValue)
            is Double -> CypressDouble(value + otherValue)
            else -> throw CypressError.CypressTypeError("Unable to add ${otherValue::class.simpleName} to Int.")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}