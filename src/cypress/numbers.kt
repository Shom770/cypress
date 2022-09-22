package cypress

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

        override fun toString(): String {
            return value.toString()
        }
    }

    class CypressDouble(override val value: Double): CypressNumber() {
        operator fun plus(other: CypressNumber): CypressNumber {
            return CypressDouble(value + other.value.toDouble())
        }

        override fun toString(): String {
            return value.toString()
        }
    }
}

fun main() {
    println(CypressNumber.CypressDouble(2.3) + CypressNumber.CypressInt(3))
}