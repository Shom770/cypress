import java.io.File

import cypress.Lexer

fun main() {
    val bufferedReader = File("src/test.cy").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val lexer = Lexer(text)
    for (token in lexer.tokenize()) {
        println(token.text(text))
    }
}