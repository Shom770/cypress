package cypress.interpreter.types

class CypressString(override val value: String) : CypressType<String> {
    operator fun plus(other: CypressString): CypressString {
        return CypressString(value + other.value)
    }

    operator fun minus(other: CypressString): CypressString {
        return CypressString(value.removeSuffix(other.value))
    }

    operator fun times(other: CypressInt): CypressString {
        return CypressString(value.repeat(other.value))
    }

    override fun toString(): String {
        return value
    }
}