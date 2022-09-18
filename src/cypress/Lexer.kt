package cypress


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private var currentChar: Char? = null
    private var position = -1

    private fun advance(): Int {
        try {
            position += 1
            currentChar = text[position]
        }
        catch (e: IndexOutOfBoundsException) {
            currentChar = null
        }

        return position
    }

    private fun peekAhead(): Char? {
        return try {
            text[position + 1]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    fun tokenize(): MutableList<Token> {
        advance()

        while (currentChar != null) {
            if (currentChar!!.isWhitespace()) {
                advance()
            }
            else if (currentChar!! in "+-/()*" && peekAhead() != '*') {
                val tokenKind = when (currentChar) {
                    '+' -> TokenType.PLUS
                    '-' -> TokenType.MINUS
                    '/' -> TokenType.FORWARD_SLASH
                    '(' -> TokenType.OPEN_PAREN
                    ')' -> TokenType.CLOSE_PAREN
                    '*' -> TokenType.ASTERISK
                    else -> {
                        throw RuntimeException()
                    }
                }

                tokens.add(Token(kind=tokenKind, span=position..position))
                advance()
            }
            else if (currentChar == '*' && peekAhead() == '*') {
                tokens.add(Token(kind=TokenType.DOUBLE_ASTERISK, span=position..advance()))
                advance()
            }
        }

        return tokens
    }
}
