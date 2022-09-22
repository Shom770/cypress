package cypress

import kotlin.math.pow

sealed class CypressNumber {
    abstract val value: Number

    override fun toString(): String {
        return value.toString()
    }

    class CypressInt(override val value: Int): CypressNumber() {
        operator fun plus(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value + other.value)
                is CypressDouble -> CypressDouble(value + other.value)
            }
        }

        operator fun minus(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value - other.value)
                is CypressDouble -> CypressDouble(value - other.value)
            }
        }

        operator fun times(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value * other.value)
                is CypressDouble -> CypressDouble(value * other.value)
            }
        }

        operator fun div(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt(value / other.value)
                is CypressDouble -> CypressDouble(value / other.value)
            }
        }

        fun pow(other: CypressNumber): CypressNumber {
            return when (other) {
                is CypressInt -> CypressInt((value.toDouble().pow(other.value)).toInt())
                is CypressDouble -> CypressDouble(value.toDouble().pow(other.value))
            }
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    class CypressDouble(override val value: Double): CypressNumber() {
        operator fun plus(other: CypressNumber): CypressNumber {
            return CypressDouble(value + other.value.toDouble())
        }

        operator fun minus(other: CypressNumber): CypressNumber {
            return CypressDouble(value - other.value.toDouble())
        }

        operator fun times(other: CypressNumber): CypressNumber {
            return CypressDouble(value * other.value.toDouble())
        }

        operator fun div(other: CypressNumber): CypressNumber {
            return CypressDouble(value / other.value.toDouble())
        }

        fun pow(other: CypressNumber): CypressNumber {
            return CypressDouble(value.pow(other.value.toDouble()))
        }

        override fun toString(): String {
            return value.toString()
        }
    }
}
