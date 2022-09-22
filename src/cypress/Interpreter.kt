package cypress

import kotlin.math.pow

class Interpreter(private val nodes: List<Node>, private val text: String) {
    fun walk(): MutableList<Any?> {
        var result = mutableListOf<Any?>()

        for (node in nodes) {
            result.add(
                when (node) {
                    is Node.BinOpNode -> parseNode(node)
                    is Node.IntNode -> parseNode(node)
                    is Node.DoubleNode -> parseNode(node)
                    is Node.UnaryOpNode -> parseNode(node)
                }
            )
        }

        return result
    }

    private fun parseNode(node: Node.IntNode): CypressNumber.CypressInt {
        return node.value
    }

    private fun parseNode(node: Node.DoubleNode): CypressNumber.CypressDouble {
        return node.value
    }

    private fun parseNode(node: Node.BinOpNode): CypressNumber {
        val leftNodeResult = when (node.leftNode) {
            is Node.IntNode-> parseNode(node.leftNode)
            is Node.DoubleNode -> parseNode(node.leftNode)
            is Node.BinOpNode -> parseNode(node.leftNode)
            is Node.UnaryOpNode -> parseNode(node.leftNode)
        }
        val rightNodeResult = when (node.rightNode) {
            is Node.IntNode-> parseNode(node.rightNode)
            is Node.DoubleNode -> parseNode(node.rightNode)
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

    private fun parseNode(node: Node.UnaryOpNode): CypressNumber {
        val nodeResult = when (node.node) {
            is Node.IntNode -> parseNode(node.node)
            is Node.DoubleNode -> parseNode(node.node)
            is Node.BinOpNode -> parseNode(node.node)
            is Node.UnaryOpNode -> parseNode(node.node)
        }

        if (node.sign.kind == TokenType.MINUS) {
            return when (nodeResult) {
                is CypressNumber.CypressInt -> -nodeResult
                is CypressNumber.CypressDouble -> -nodeResult
            }
        }
        return nodeResult
    }
}