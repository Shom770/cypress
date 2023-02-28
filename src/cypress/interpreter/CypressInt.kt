package cypress.interpreter

class CypressInt(override val value: Int) : CypressType<Int> {
    constructor(value: String) : this(value.toInt())

    override fun toString(): String {
        return value.toString()
    }
}