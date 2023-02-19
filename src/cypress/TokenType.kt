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
    PLUS(1),
    MINUS(1),
    ASTERISK(3),
    FORWARD_SLASH(3),
    DOUBLE_ASTERISK(5),
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
    PROC,

    // delimiters for end of line/file
    NEWLINE,
    EOF
}