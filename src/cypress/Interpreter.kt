package cypress

import kotlin.math.pow

class Interpreter(private val nodes: List<Node>, private val text: String) {
    fun walk(): MutableList<Any?> {
        var result = mutableListOf<Any?>()

        for (node in nodes) {
            result.add(
                when (node) {
                    is Node.BinOpNode -> parseNode(node)
                    is Node.NumberNode -> parseNode(node)
                    is Node.UnaryOpNode -> parseNode(node)
                }
            )
        }

        return result
    }

    private fun parseNode(node: Node.NumberNode): Double {
        return node.token.text(text).toDouble()
    }

    private fun parseNode(node: Node.BinOpNode): Double {
        val leftNodeResult = when (node.leftNode) {
            is Node.NumberNode -> parseNode(node.leftNode)
            is Node.BinOpNode -> parseNode(node.leftNode)
            is Node.UnaryOpNode -> parseNode(node.leftNode)
        }
        val rightNodeResult = when (node.rightNode) {
            is Node.NumberNode -> parseNode(node.rightNode)
            is Node.BinOpNode -> parseNode(node.rightNode)
            is Node.UnaryOpNode -> parseNode(node.rightNode)
        }

        return when (node.opToken.text(text)) {
            "+" -> leftNodeResult + rightNodeResult
            "-" -> leftNodeResult - rightNodeResult
            "*" -> leftNodeResult * rightNodeResult
            "/" -> leftNodeResult / rightNodeResult
            "**" -> leftNodeResult.pow(rightNodeResult)
            else -> throw RuntimeException("Invalid operator: ${node.opToken.text(text)}")
        }
    }

    private fun parseNode(node: Node.UnaryOpNode): Double {
        val nodeResult = when (node.node) {
            is Node.NumberNode -> parseNode(node.node)
            is Node.BinOpNode -> parseNode(node.node)
            is Node.UnaryOpNode -> parseNode(node.node)
        }

        if (node.sign.kind == TokenType.MINUS) {
            return nodeResult * -1.0
        }
        return nodeResult
    }
}