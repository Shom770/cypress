package cypress.interpreter.types

import cypress.CypressError
import kotlin.math.pow

class CypressDouble(override val value: Double) : CypressNumber {
    constructor(value: String) : this(value.toDouble())

    override operator fun plus(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value + otherValue)
            is Double -> CypressDouble(value + otherValue)
            else -> throw CypressError.CypressTypeError("Unable to add ${otherValue::class.simpleName} to Double.")
        }
    }

    override operator fun minus(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value - otherValue)
            is Double -> CypressDouble(value - otherValue)
            else -> throw CypressError.CypressTypeError("Unable to subtract ${otherValue::class.simpleName} to Double.")
        }
    }

    override operator fun times(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value * otherValue)
            is Double -> CypressDouble(value * otherValue)
            else -> throw CypressError.CypressTypeError("Unable to multiply ${otherValue::class.simpleName} to Double.")
        }
    }

    override operator fun div(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value / otherValue)
            is Double -> CypressDouble(value / otherValue)
            else -> throw CypressError.CypressTypeError("Unable to divide ${otherValue::class.simpleName} to Double.")
        }
    }

    override fun pow(other: CypressNumber): CypressDouble {
        return when (val otherValue = other.value) {
            is Int -> CypressDouble(value.pow(otherValue))
            is Double -> CypressDouble(value.pow(otherValue))
            else -> throw CypressError.CypressTypeError("Unable to pow ${otherValue::class.simpleName} to Int.")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}