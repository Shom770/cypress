package cypress

enum class TokenType {
    // symbols
    OPEN_PAREN,
    CLOSE_PAREN,
    ASSIGN,
    OPEN_BRACE,
    CLOSE_BRACE,
    EQUALS,
    NOT_EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_OR_EQ,
    GREATER_THAN_OR_EQ,
    DOUBLE_AMPERSAND,
    DOUBLE_PIPE,

    // operators
    PLUS,
    MINUS,
    ASTERISK,
    FORWARD_SLASH,
    DOUBLE_ASTERISK,

    // identifiers and literals
    INT,
    FLOAT,
    IDENTIFIER,

    // keywords
    IF,
    ELSE
}