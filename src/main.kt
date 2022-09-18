import java.io.File

import cypress.Lexer

fun main() {
    for (line in File("src/test.cy").readLines()) {
        val lexer = Lexer(line)
    }
}