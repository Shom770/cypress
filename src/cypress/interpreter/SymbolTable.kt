package cypress.interpreter

import cypress.CypressError
import cypress.interpreter.types.CypressInt
import cypress.interpreter.types.CypressType

class SymbolTable(private val parent: SymbolTable? = null) {
    val underlyingTable = mutableMapOf<String, CypressType<Any>>()

    init {
        underlyingTable["true"] = CypressInt(1) as CypressType<Any>  // Always true, Kotlin smart casting is dumb
        underlyingTable["false"] = CypressInt(0) as CypressType<Any>
    }

    fun <T : CypressType<Any>> setVariable(name: String, underlyingValue: T) {
        underlyingTable[name] = underlyingValue
    }

    fun lookupVariable(name: String) : CypressType<Any> {
        return underlyingTable[name] ?: (
            parent?.lookupVariable(name) ?: throw CypressError.CypressVariableNotFoundError(
                "Unable to find variable $name, are you sure you set it?"
            )
        )
    }
}