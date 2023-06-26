package cypress.interpreter.types

sealed interface CypressArithmeticTypes : CypressType<Any> {
    abstract class CNumber : CypressNumber
    abstract class CString : CypressType<String>
}