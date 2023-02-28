package cypress.interpreter

class CypressDouble(override val value: Double) : CypressType<Double> {
    constructor(value: String) : this(value.toDouble())

    override fun toString(): String {
        return value.toString()
    }
}