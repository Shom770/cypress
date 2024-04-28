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

    override fun compareTo(other: CypressType<String>): CypressInt {
        return CypressInt(value.compareTo(other.value))
    }

    override fun asBoolean(): CypressInt {
        return CypressInt(value.isNotEmpty())
    }

    // Built-in methods for the String type
    fun concat(other: CypressString): CypressString {
        return CypressString(value + other.value)
    }

    fun replace(original: CypressString, new: CypressString): CypressString {
        return CypressString(value.replace(original.value, new.value))
    }
}