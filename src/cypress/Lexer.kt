package cypress


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private val textIterator = text.toList().listIterator()

    private val validDigits = "0123456789."
    private val validIdentifiers = ('A'..'z').joinToString("").replace("[\\]^_`", "_")
    private val validOperators = "+-*/"
    private val miscCharacters = "()="

    private fun advance(n: Int = 1): IndexedValue<Char> {
        // Deal with the case where when advancing through the text, the amount of times to advance is 0.
        if (n <= 0) {
            return IndexedValue(textIterator.nextIndex() - 1, text[textIterator.nextIndex() - 1])
        }

        repeat(n - 1) { textIterator.next() }
        return IndexedValue(textIterator.nextIndex(), textIterator.next())
    }

    private fun peekAhead(): Char? {
        return text.getOrNull(textIterator.nextIndex())
    }

    private fun parseOperator(index: Int, currentChar: Char): Token {
        return when (currentChar) {
            '+' -> Token(TokenType.PLUS, span = index until advance().index)
            '-' -> Token(TokenType.MINUS, span = index until advance().index)
            '/' -> Token(TokenType.FORWARD_SLASH, span = index until advance().index)
            '*' -> {
                if (peekAhead() == '*') {
                    Token(TokenType.DOUBLE_ASTERISK, span = index until advance(2).index)
                } else {
                    Token(TokenType.ASTERISK, span = index until advance().index)
                }
            }
            else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar")
        }
    }

    private fun parseIdentifier(index: Int): Token {
        val endOfIdentifier = text.substring(
            index until text.length
        ).indexOfFirst {
            it !in validIdentifiers
        }.takeIf {
            it >= 0
        } ?: (text.length - index)
        val spanOfIdentifier = index until endOfIdentifier + index

        return Token(TokenType.IDENTIFIER, span = spanOfIdentifier).also {
            advance(spanOfIdentifier.last - spanOfIdentifier.first)
        }
    }

    private fun parseNumber(index: Int): Token {
        val endOfNumber = text.substring(
            index until text.length
        ).indexOfFirst {
            it !in validDigits
        }.takeIf {
            it >= 0
        } ?: (text.length - index)
        val spanOfNumber = index until endOfNumber + index

        val decimalPointCount = text.substring(spanOfNumber).count { it == '.' }
        if (decimalPointCount > 1) {
            throw CypressError.CypressSyntaxError(
                "Found $decimalPointCount decimal points within this number: ${text.substring(spanOfNumber)}."
            )
        }

        return Token(if (decimalPointCount == 0) TokenType.INT else TokenType.FLOAT, span = spanOfNumber).also {
            advance(spanOfNumber.last - spanOfNumber.first)
        }
    }

    private fun parseMiscCharacters(index: Int, currentChar: Char): Token {
        return when (currentChar) {
            '(' -> Token(TokenType.OPEN_PAREN, span = index until index + 1)
            ')' -> Token(TokenType.CLOSE_PAREN, span = index until index + 1)
            '=' -> Token(TokenType.ASSIGN, span = index until index + 1)
            else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar at index $index")
        }
    }

    fun tokenize(): MutableList<Token> {
        while (textIterator.hasNext()) {
            val (index, currentChar) = advance()

            // Deal with when we're lexing whitespace
            if (currentChar.isWhitespace()) {
                continue
            }

            tokens.add(
                when (currentChar) {
                    in validOperators -> parseOperator(index, currentChar)
                    in miscCharacters -> parseMiscCharacters(index, currentChar)
                    in validDigits -> parseNumber(index)
                    in validIdentifiers -> parseIdentifier(index)
                    else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar at index: $index")
                }
            )
        }

        return tokens
    }
}
