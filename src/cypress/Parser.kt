package cypress

class Parser(private val tokens: MutableList<Token>) {
    var position = -1
        set(value) {
            field = value
            currentToken = tokens.getOrNull(field)
        }
    var currentToken: Token? = null


}