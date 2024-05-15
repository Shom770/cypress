import java.io.File

import cypress.lexer.Lexer
import cypress.parser.Parser
import cypress.interpreter.Interpreter
import cypress.interpreter.types.CypressType
import cypress.parser.Node

fun main() {
    val bufferedReader = File("src/test.cyp").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    // Time the language
    val startTime = System.nanoTime()

    // Run the language
    val tokens = Lexer(text).tokenize()
    println(tokens)
    val parser = Parser(tokens).parseExpr().filter { it !is Node.EmptyNode }
    val interpreter = Interpreter(text)
    parser.map { interpreter.walk<CypressType<*>>(it) }

    // Print out the total time
    println("Process executed in ${(System.nanoTime() - startTime) / 1000000} ms")
}
