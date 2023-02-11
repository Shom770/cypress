import java.io.File

import cypress.*

fun main() {
    val bufferedReader = File("src/test.cyp").bufferedReader()
    val text = bufferedReader.use { it.readText() }
    val tokens = Lexer(text).tokenize()
    println(tokens)
}
