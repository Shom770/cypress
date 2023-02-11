package cypress

enum class TokenType {
    // symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    ASSIGN,

    // operators
    PLUS,
    MINUS,
    ASTERISK,
    FORWARD_SLASH,
    DOUBLE_ASTERISK,

    // identifiers and literals
    INT,
    FLOAT,
    IDENTIFIER
}