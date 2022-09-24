package cypress

import kotlin.math.pow

sealed class CypressNumber {
    abstract val value: Number

    abstract operator fun plus(other: CypressNumber): CypressNumber
    abstract operator fun minus(other: CypressNumber): CypressNumber
    abstract operator fun times(other: CypressNumber): CypressNumber
    abstract operator fun div(other: CypressNumber): CypressNumber
    abstract fun unaryMinus(): CypressNumber
    abstract fun pow(other: CypressNumber): CypressNumber
    abstract operator fun compareTo(other: CypressNumber): Int

    override fun toString(): String {
        return value.toString()
    }

    class CypressInt(override val value: Int): CypressNumber() {
        override operator fun plus(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value + other.value)
                is CypressDouble -> CypressDouble(value + other.value)
            }
        }

        override operator fun minus(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value - other.value)
                is CypressDouble -> CypressDouble(value - other.value)
            }
        }

        override operator fun times(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value * other.value)
                is CypressDouble -> CypressDouble(value * other.value)
            }
        }

        override operator fun div(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value / other.value)
                is CypressDouble -> CypressDouble(value / other.value)
            }
        }

        override operator fun unaryMinus(): CypressInt {
            return CypressInt(value * -1)
        }

        override fun pow(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt((value.toDouble().pow(other.value)).toInt())
                is CypressDouble -> CypressDouble(value.toDouble().pow(other.value))
            }
        }

        override operator fun compareTo(other: CypressNumber): Int {
            return (this - other).value as Int
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    class CypressDouble(override val value: Double): CypressNumber() {
        override operator fun plus(other: CypressNumber): CypressNumber {
            return CypressDouble(value + other.value.toDouble())
        }

        override operator fun minus(other: CypressNumber): CypressNumber {
            return CypressDouble(value - other.value.toDouble())
        }

        override operator fun times(other: CypressNumber): CypressNumber {
            return CypressDouble(value * other.value.toDouble())
        }

        override operator fun div(other: CypressNumber): CypressNumber {
            return CypressDouble(value / other.value.toDouble())
        }

        override operator fun unaryMinus(): CypressDouble {
            return CypressDouble(value * -1.0)
        }

        override fun pow(other: CypressNumber): CypressNumber {
            return CypressDouble(value.pow(other.value.toDouble()))
        }

        override operator fun compareTo(other: CypressNumber): Int {
            return (this - other).value.toInt()
        }

        override fun toString(): String {
            return value.toString()
        }
    }
}
