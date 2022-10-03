import java.io.File

import cypress.*

fun main() {
    val bufferedReader = File("src/test.cy").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val tokens = Lexer(text).tokenize()
    val nodes = Parser(tokens, text, inIfStatement = true).parse()
    println(Interpreter(nodes, text).walk())
}