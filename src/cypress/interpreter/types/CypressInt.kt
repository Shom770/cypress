package cypress.interpreter.types

import cypress.CypressError
import kotlin.math.pow

class CypressInt(override val value: Int) : CypressNumber {
    constructor(value: String) : this(value.toInt())

    constructor(value: Boolean) : this(if (value) 1 else 0)

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

    // Special operator functions for the booelan attributes of a CypressInt, maybe make this its own type?
    fun and(other: CypressInt) : CypressInt {
        return CypressInt(value >= 1 && other.value >= 1)
    }

    fun or(other: CypressInt) : CypressInt {
        return CypressInt(value >= 1 || other.value >= 1)
    }

    fun not() : CypressInt {
        return if (value == 0) CypressInt(1) else CypressInt(0)
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun compareTo(other: CypressType<Number>): CypressInt {
        return CypressInt(value.compareTo(other.value.toDouble()))
    }

    override fun asBoolean(): CypressInt {
        return CypressInt(value != 0)
    }
}