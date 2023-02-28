package cypress.interpreter

interface CypressType<T> {
    val value: T

    override fun toString(): String
}