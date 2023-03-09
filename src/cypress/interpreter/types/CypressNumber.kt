package cypress.interpreter.types

interface CypressNumber: CypressType<Number> {
    override val value: Number

    operator fun plus(other: CypressNumber)
    override fun toString() : String
}