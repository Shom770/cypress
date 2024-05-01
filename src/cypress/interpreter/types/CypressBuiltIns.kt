package cypress.interpreter.types

object CypressBuiltIns {
    fun print(obj: CypressType<Any>) {
        println(obj.value)
    }
}