package cypress


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private var currentChar: Char? = null
    private var position = -1
        set(value) {
            field = value
            currentChar = text.getOrNull(value)
        }
 
    private fun peekAhead(): Char? {
        return text.getOrNull(position + 1)
    }

    fun tokenize(): MutableList<Token> {
    }
}
