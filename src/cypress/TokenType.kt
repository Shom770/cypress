package cypress

enum class TokenType {
    // symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACE,
    CLOSE_BRACE,
    COMMA,
    ASSIGN,
    ARROW,

    // operators
    PLUS,
    MINUS,
    ASTERISK,
    FORWARD_SLASH,
    DOUBLE_ASTERISK,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN_OR_EQUAL,
    EQUALS,

    // identifiers and literals
    INT,
    FLOAT,
    IDENTIFIER,

    // keywords
    IF,
    ELSE,
    FOR,
    PROC
}