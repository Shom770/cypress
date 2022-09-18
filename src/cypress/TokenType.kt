package cypress

enum class TokenType {
    // misc
    OPEN_PAREN,
    CLOSE_PAREN,

    // operators
    PLUS,
    MINUS,
    ASTERISK,
    FORWARD_SLASH,
    DOUBLE_ASTERISK,

    // identifiers and literals
    INT,
    FLOAT
}