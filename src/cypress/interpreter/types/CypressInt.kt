package cypress.interpreter.types

import cypress.CypressError
import kotlin.math.pow

class CypressInt(override val value: Int) : CypressNumber {
    constructor(value: String) : this(value.toInt())

    override operator fun plus(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(value + otherValue)
            is Double -> CypressDouble(value + otherValue)
            else -> throw CypressError.CypressTypeError("Unable to add ${otherValue::class.simpleName} to Int.")
        }
    }

    override operator fun minus(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(value - otherValue)
            is Double -> CypressDouble(value - otherValue)
            else -> throw CypressError.CypressTypeError("Unable to subtract ${otherValue::class.simpleName} to Int.")
        }
    }

    override operator fun times(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(value * otherValue)
            is Double -> CypressDouble(value * otherValue)
            else -> throw CypressError.CypressTypeError("Unable to multiply ${otherValue::class.simpleName} to Int.")
        }
    }

    override operator fun div(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(value / otherValue)
            is Double -> CypressDouble(value / otherValue)
            else -> throw CypressError.CypressTypeError("Unable to divide ${otherValue::class.simpleName} to Int.")
        }
    }

    override fun pow(other: CypressNumber): CypressNumber {
        return when (val otherValue = other.value) {
            is Int -> CypressInt(
                value
                    .toDouble()
                    .pow(otherValue)
                    .toInt()
            )
            is Double -> CypressDouble(
                value
                    .toDouble()
                    .pow(otherValue)
            )
            else -> throw CypressError.CypressTypeError("Unable to pow ${otherValue::class.simpleName} to Int.")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}