package cypress


class Lexer(val text: String) {
    val tokens = mutableListOf<Token>()
    private var current_char: Char? = null
    private var position = -1

    private fun advance() {
        try {
            position += 1
            current_char = text[position]
        }
        catch (e: IndexOutOfBoundsException) {
            current_char = null
        }

    }

    fun tokenize(): MutableList<Token> {
        advance()

        while (current_char != null) {
            if (current_char!!.isWhitespace()) {
                continue
            }
            else if (current_char == '+') {
                tokens.add(Token(kind=TokenType.PLUS, span=position..position))
                advance()
            }
        }

        return tokens
    }
}
