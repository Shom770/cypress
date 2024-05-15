package cypress.lexer

enum class TokenType(val precedence: Int = 0){
    // symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACE,
    CLOSE_BRACE,
    COMMA,
    ASSIGN,
    ARROW,
    DOT,

    // operators
    PLUS,
    MINUS,
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
    STRING,

    // keywords
    NOT(11),
    AND,
    OR,
    IF,
    ELSE,
    ELIF,
    FOR,
    PROC,

    // delimiters for end of line/file
    NEWLINE,
    EOF;

    companion object {
        val conditionalTokens = hashSetOf(
            LESS_THAN,
            LESS_THAN_OR_EQUAL,
            GREATER_THAN,
            GREATER_THAN_OR_EQUAL,
            EQUALS,
            AND,
            OR
        )
    }
}