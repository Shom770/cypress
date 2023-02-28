package cypress.lexer


class Token(val kind: TokenType, private val span: IntRange) {
    fun text(inputString: String): String {
        return inputString.substring(span)
    }

    override fun toString(): String {
        return "Token(kind = $kind, span = $span)"
    }
}
