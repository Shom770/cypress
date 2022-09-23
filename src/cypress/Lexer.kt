package cypress


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private var currentChar: Char? = null
    private var position = -1
        set(value) {
            field = value
            currentChar = text.getOrNull(value)
        }
    private val keywordMapping = hashMapOf(
        "if" to TokenType.IF
    )

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
            else if (currentChar!! in "()=<>!") {
                tokens.add(lexSymbol())
                position += 1
            }
            else if (currentChar!!.isDigit() || currentChar == '.') {
                tokens.add(lexNumber())
                position += 1
            }
            else if (currentChar!!.isLetter()) {
                tokens.add(lexIdentifierOrKeyword())
                position += 1
            }
        }

        return tokens
    }

    private fun lexSymbol(): Token {
        val tokenKind = if (peekAhead() == '=') {
            when (currentChar) {
                '<' -> TokenType.LESS_THAN_OR_EQ
                '>' -> TokenType.GREATER_THAN_OR_EQ
                '=' -> TokenType.EQUALS
                '!' -> TokenType.NOT_EQUALS
                else -> throw RuntimeException("$currentChar is not a valid character.")
            }.also { position += 1 }
        }
        else {
            when (currentChar) {
                '(' -> TokenType.OPEN_PAREN
                ')' -> TokenType.CLOSE_PAREN
                '=' -> TokenType.ASSIGN
                '<' -> TokenType.LESS_THAN
                '>' -> TokenType.GREATER_THAN
                else -> throw RuntimeException("$currentChar is not a valid character.")
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

    private fun lexIdentifierOrKeyword(): Token {
        var startingPosition = position

        while (currentChar != null && currentChar!!.isLetterOrDigit()) {
            position += 1
        }

        position -= 1

        val keywordText = text.slice(startingPosition..position)

        return if (keywordText in keywordMapping.keys) {
            Token(
                kind = keywordMapping[keywordText] ?: throw RuntimeException("Invalid keyword: '$keywordText'"),
                span = startingPosition..position
            )
        } else {
            Token(kind = TokenType.IDENTIFIER, span = startingPosition..position)
        }
    }
}
