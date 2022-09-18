package cypress

import cypress.TokenType

class Lexer(val text: String) {
    val tokens = mutableListOf<Token>()
}