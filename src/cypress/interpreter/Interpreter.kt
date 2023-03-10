package cypress.interpreter

import cypress.CypressError
import cypress.interpreter.types.CypressDouble
import cypress.interpreter.types.CypressInt
import cypress.interpreter.types.CypressNumber
import cypress.lexer.TokenType
import cypress.parser.Node

class Interpreter(private val sourceText: String) {
    fun <T> walk(node: Node): T {
        return when (node) {
            is Node.ArithmeticNode -> parseArithmeticNode(node)
            is Node.NumberNode -> parseNumberNode(node)
            else -> throw CypressError.CypressTypeError("Can't process node $node")
        }
    }

    private inline fun <reified T : CypressNumber> parseArithmeticNode(node: Node.ArithmeticNode): T {
        val leftNode = walk<CypressNumber>(node.leftNode)
        val rightNode = walk<CypressNumber>(node.rightNode)

        return when (node.operator) {
            TokenType.PLUS -> leftNode + rightNode
            TokenType.MINUS -> leftNode - rightNode
            TokenType.ASTERISK -> leftNode * rightNode
            TokenType.FORWARD_SLASH -> leftNode / rightNode
            TokenType.DOUBLE_ASTERISK -> leftNode.pow(rightNode)
            else -> throw CypressError.CypressSyntaxError("Invalid token in expression : ${node.operator}")
        } as T
    }

    private inline fun <reified T : CypressNumber> parseNumberNode(node: Node.NumberNode): T {
        return when (node.underlyingToken.kind) {
            TokenType.INT -> CypressInt(node.underlyingToken.text(sourceText))
            TokenType.FLOAT -> CypressDouble(node.underlyingToken.text(sourceText))
            else -> throw CypressError.CypressTypeError(
                "Underlying token for number node ${node.underlyingToken} is invalid."
            )
        } as T
    }
}
