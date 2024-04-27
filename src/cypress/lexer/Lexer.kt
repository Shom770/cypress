package cypress.lexer

import cypress.CypressError


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private val textIterator = text.toList().listIterator()

    private val validOperators = "+-*/<>"
    private val miscCharacters = "(){},=."
    private val validDigits = "0123456789."
    private val validIdentifiers = ('A'..'z').joinToString("").replace("[\\]^_`", "_")
    private val keywords = hashMapOf(
        "not" to TokenType.NOT,
        "and" to TokenType.AND,
        "or" to TokenType.OR,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "for" to TokenType.FOR,
        "proc" to TokenType.PROC
    )

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

    private fun lexOperator(index: Int, currentChar: Char): Token {
        return when (currentChar) {
            '+' -> Token(TokenType.PLUS, span = index..index)
            '/' -> Token(TokenType.FORWARD_SLASH, span = index..index)
            // Defining behavior for the operators -, ->, *, **, <, <=, >, >=
            '-' -> {
                if (peekAhead() == '>') {
                    Token(TokenType.ARROW, span = index..advance().index)
                }
                else {
                    Token(TokenType.MINUS, span = index..index)
                }
            }
            '*' -> {
                if (peekAhead() == '*') {
                    Token(TokenType.DOUBLE_ASTERISK, span = index..advance().index)
                } else {
                    Token(TokenType.ASTERISK, span = index..index)
                }
            }
            '<' -> {
                if (peekAhead() == '=') {
                    Token(TokenType.LESS_THAN_OR_EQUAL, span = index..advance().index)
                } else {
                    Token(TokenType.LESS_THAN, span = index..index)
                }
            }
            '>' -> {
                if (peekAhead() == '=') {
                    Token(TokenType.GREATER_THAN_OR_EQUAL, span = index..advance().index)
                } else {
                    Token(TokenType.GREATER_THAN, span = index..index)
                }
            }
            else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar")
        }
    }

    private fun lexIdentifier(index: Int): Token {
        val endOfIdentifier = text.substring(
            index until text.length
        ).indexOfFirst {
            it !in validIdentifiers
        }.takeIf {
            it >= 0
        } ?: (text.length - index)
        val spanOfIdentifier = index until endOfIdentifier + index

        return Token(
            keywords.getOrDefault(text.substring(spanOfIdentifier).lowercase(), TokenType.IDENTIFIER),
            span = spanOfIdentifier
        ).also {
            advance(spanOfIdentifier.last - spanOfIdentifier.first)
        }
    }

    private fun lexNumber(index: Int): Token {
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

    private fun lexString(index: Int): Token {
        val endOfString = text.substring(
            index until text.length
        ).indexOfFirst {
            it == '"'
        }.takeIf {
            it >= 0
        } ?: (text.length - index)
        val spanOfString = index until endOfString + index

        return Token(TokenType.STRING, spanOfString).also { advance(spanOfString.last - spanOfString.first) }
    }

    private fun lexMiscCharacters(index: Int, currentChar: Char): Token {
        return when (currentChar) {
            '(' -> Token(TokenType.OPEN_PAREN, span = index..index)
            ')' -> Token(TokenType.CLOSE_PAREN, span = index..index)
            '{' -> Token(TokenType.OPEN_BRACE, span = index..index)
            '}' -> Token(TokenType.CLOSE_BRACE, span = index..index)
            ',' -> Token(TokenType.COMMA, span = index..index)
            '.' -> Token(TokenType.DOT, span = index..index)
            '=' -> {
                if (peekAhead() == '=') {
                    Token(TokenType.EQUALS, span = index..advance().index)
                }
                else {
                    Token(TokenType.ASSIGN, span = index..index)
                }
            }
            else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar at index $index")
        }
    }

    fun tokenize(): MutableList<Token> {
        while (textIterator.hasNext()) {
            val (index, currentChar) = advance()

            // Deal with when we're lexing whitespace
            if (!currentChar.toString().matches(Regex("."))) {
                tokens.add(Token(TokenType.NEWLINE, span = index..index))
                continue
            }
            else if (currentChar.isWhitespace()) {
                continue
            }

            tokens.add(
                when (currentChar) {
                    '"' -> {
                        advance(1)
                        lexString(index + 1).also { advance(1) }
                    }
                    in validOperators -> lexOperator(index, currentChar)
                    in miscCharacters -> lexMiscCharacters(index, currentChar)
                    in validDigits -> lexNumber(index)
                    in validIdentifiers -> lexIdentifier(index)
                    else -> throw CypressError.CypressSyntaxError("Invalid character: $currentChar at index: $index")
                }
            )
        }

        return tokens
    }
}
