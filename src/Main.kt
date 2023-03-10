import java.io.File

import cypress.lexer.Lexer
import cypress.parser.Parser
import cypress.interpreter.Interpreter

fun main() {
    val bufferedReader = File("src/test.cyp").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val tokens = Lexer(text).tokenize()
    val parser = Parser(tokens).parseExpr()
    val interpreter = Interpreter(text)

    println(parser.map { interpreter.walk<Any>(it) })
}
