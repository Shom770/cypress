package cypress


class Lexer(val text: String) {
    private val tokens = mutableListOf<Token>()
    private var currentChar: Char? = null
    private var position = -1

    private fun advance() {
        try {
            position += 1
            currentChar = text[position]
        }
        catch (e: IndexOutOfBoundsException) {
            currentChar = null
        }

    }

    fun tokenize(): MutableList<Token> {
        advance()

        while (currentChar != null) {
            if (currentChar!!.isWhitespace()) {
                continue
            }
            else if (currentChar == '+') {
                tokens.add(Token(kind=TokenType.PLUS, span=position..position))
                advance()
            }
        }

        return tokens
    }
}
