package cypress


class Token(val kind: TokenType, private val span: IntRange) {
    fun text(input_str: String): String {
        return input_str.slice(span)
    }

    override fun toString(): String {
        return "Token(kind=$kind, span=$span)"
    }
}
