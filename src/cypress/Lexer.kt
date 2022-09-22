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
            else if (currentChar!! in "()=") {
                tokens.add(lexMisc())
                position += 1
            }
            else if (currentChar!!.isDigit() || currentChar == '.') {
                tokens.add(lexNumber())
                position += 1
            }
            else if (currentChar!!.isLetter()) {
                tokens.add(lexIdentifier())
                position += 1
            }
        }

        return tokens
    }

    private fun lexMisc(): Token {
        val tokenKind = when (currentChar) {
            '(' -> TokenType.OPEN_PAREN
            ')' -> TokenType.CLOSE_PAREN
            '=' -> TokenType.ASSIGN
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

    private fun lexNumber(): Token {
        val startingPosition = position
        var decimalCount = 0

        while (currentChar != null && (currentChar!!.isDigit() || currentChar == '.')) {
            if (currentChar == '.') decimalCount += 1
            position += 1
        }

        position -= 1

        if (decimalCount > 1) {
            throw ArithmeticException("Had $decimalCount decimal points in float.")
        }

        return Token(
            kind = if (decimalCount == 0) TokenType.INT else TokenType.FLOAT,
            span = startingPosition..position
        )
    }

    private fun lexIdentifier(): Token {
        var startingPosition = position

        while (currentChar != null && currentChar!!.isLetterOrDigit()) {
            position += 1
        }

        position -= 1

        return Token(kind = TokenType.IDENTIFIER, span = startingPosition..position)
    }
}
