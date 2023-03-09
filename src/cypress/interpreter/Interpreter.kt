package cypress.interpreter

import cypress.CypressError
import cypress.interpreter.types.CypressNumber
import cypress.interpreter.types.CypressType
import cypress.lexer.TokenType
import cypress.parser.Node

class Interpreter(private val sourceText: String) {
    fun walk(node: Node): CypressType<out Any> {
        return when (node) {
            is Node.ArithmeticNode -> parseArithmeticNode(node)
            else -> throw CypressError.CypressTypeError("Can't process node $node")
        }
    }

    private fun parseArithmeticNode(node: Node.ArithmeticNode): CypressNumber {
        val leftNode = walk(node.leftNode)
        val rightNode = walk(node.rightNode)

        if (leftNode !is CypressNumber || rightNode !is CypressNumber) {
            throw CypressError.CypressTypeError("Invalid nodes: $leftNode and $rightNode")
        }

        when (node.operator) {
            TokenType.PLUS -> leftNode + rightNode
            TokenType.MINUS ->
        }
    }
}

fun main() {
}