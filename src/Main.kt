import java.io.File

import cypress.lexer.Lexer
import cypress.parser.Parser
import cypress.interpreter.Interpreter
import cypress.interpreter.types.CypressType
import cypress.parser.Node

fun main() {
    val bufferedReader = File("src/test.cyp").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val tokens = Lexer(text).tokenize()
    val parser = Parser(tokens).parseExpr().filter { it !is Node.EmptyNode }
    println(parser)
    val interpreter = Interpreter(text)
    parser.map { interpreter.walk<CypressType<*>>(it) }
}
