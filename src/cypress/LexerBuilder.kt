package cypress

// The configuration for each token
data class TokenConfiguration(val tokenString: String, val tokenType: TokenType) {
    init {

    }
}

class LexerBuilder(builder: LexerBuilder.() -> Unit) {
    private val tokenConfigs = mutableListOf<TokenConfiguration>()

    init {
        builder()
        println(tokenConfigs)
    }

    infix fun String.means(tokenType: TokenType) {
        tokenConfigs.add(TokenConfiguration(this, tokenType))
    }

    // Function that must be ran in order for the builder to work.
    fun rules(tokenRules: () -> Unit) {
        tokenRules()
    }
}

fun main() {
    LexerBuilder {
       rules {
           "(" means TokenType.OPEN_PAREN
           ")" means TokenType.CLOSE_PAREN
           "+" means TokenType.PLUS
       }
    }
}