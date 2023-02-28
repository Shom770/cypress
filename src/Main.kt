import java.io.File

import cypress.lexer.Lexer
import cypress.parser.Parser

fun main() {
    val bufferedReader = File("src/test.cyp").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val tokens = Lexer(text).tokenize()
    val parser = Parser(tokens)
    println(parser.parseExpr())
}
