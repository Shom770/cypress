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
        position += 1

        while (currentChar != null) {
            if (currentChar!!.isWhitespace()) {
                position += 1
            }
            else if (currentChar!! in "+-/()*") {
                tokens.add(lexOperator())
                position += 1
            }
            else if (currentChar!! in "()") {
                tokens.add(lexMisc())
                position += 1
            }
        }

        return tokens
    }

    private fun lexMisc(): Token {
        val tokenKind = when (currentChar) {
            '(' -> TokenType.OPEN_PAREN
            ')' -> TokenType.CLOSE_PAREN
            else -> {
                throw RuntimeException("$currentChar is not a valid character.")
            }
        }

        return Token(kind = tokenKind, span = position..position)
    }
    private fun lexOperator(): Token {
        if (currentChar == '*' && peekAhead() == '*') {
            position += 1
            return Token(kind = TokenType.DOUBLE_ASTERISK, span = position - 1..position)
        }
        else {
            val tokenKind = when (currentChar) {
                '+' -> TokenType.PLUS
                '-' -> TokenType.MINUS
                '/' -> TokenType.FORWARD_SLASH
                '(' -> TokenType.OPEN_PAREN
                ')' -> TokenType.CLOSE_PAREN
                '*' -> TokenType.ASTERISK
                else -> {
                    throw RuntimeException("$currentChar is not a valid character.")
                }
            }
            return Token(kind = tokenKind, span = position..position)
        }
    }
}
