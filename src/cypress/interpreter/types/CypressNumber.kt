package cypress.interpreter.types

interface CypressNumber: CypressType<Number> {
    override val value: Number

    operator fun plus(other: CypressNumber): CypressNumber

    operator fun minus(other: CypressNumber): CypressNumber

    operator fun times(other: CypressNumber): CypressNumber

    operator fun div(other: CypressNumber): CypressNumber

    fun pow(other: CypressNumber): CypressNumber

    override fun toString() : String
}