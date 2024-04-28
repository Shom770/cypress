package cypress.interpreter.types

class CypressNull(override val value: Nothing?): CypressType<Nothing?> {
    override fun compareTo(other: CypressType<Nothing?>): CypressInt {
        return CypressInt(1)
    }

    override fun asBoolean(): CypressInt {
        return CypressInt(0)
    }
}