package cypress

enum class TokenType(val precedence: Int = 0){
    // symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACE,
    CLOSE_BRACE,
    COMMA,
    ASSIGN,
    ARROW,

    // operators
    PLUS(3),
    MINUS(3),
    ASTERISK(5),
    FORWARD_SLASH(5),
    DOUBLE_ASTERISK(7),
    LESS_THAN(1),
    GREATER_THAN(1),
    LESS_THAN_OR_EQUAL(1),
    GREATER_THAN_OR_EQUAL(1),
    EQUALS(1),

    // identifiers and literals
    INT,
    FLOAT,
    IDENTIFIER,

    // keywords
    NOT,
    AND(11),
    OR(9),
    IF,
    ELSE,
    FOR,
    PROC,

    // delimiters for end of line/file
    NEWLINE,
    EOF;

    companion object {
        val conditionalTokens = hashSetOf(
            TokenType.LESS_THAN,
            TokenType.LESS_THAN_OR_EQUAL,
            TokenType.GREATER_THAN,
            TokenType.GREATER_THAN_OR_EQUAL,
            TokenType.EQUALS,
            TokenType.AND,
            TokenType.OR
        )
    }
}