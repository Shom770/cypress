package cypress

class Interpreter(private val nodes: List<Node>) {
    var position = -1
        set(value) {
            field = value
            currentToken = tokens.getOrNull(field)
        }
    var currentToken: Token? = null
}