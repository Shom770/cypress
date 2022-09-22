package cypress

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

    private fun parseNode(node: Node.NumberNode): Number {
        return if (node.token.kind == TokenType.FLOAT) {
            node.token.text(text).toDouble()
        } else {
            node.token.text(text).toInt()
        }
    }

    private fun parseNode(node: Node.BinOpNode) {
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

        when (node.opToken.text(text)) {
            "+" -> leftNodeResult + rightNodeResult
        }
    }

    private fun parseNode(node: Node.UnaryOpNode): Any {
        val nodeResult = when (node.node) {
            is Node.NumberNode -> parseNode(node.node)
            is Node.BinOpNode -> parseNode(node.node)
            is Node.UnaryOpNode -> parseNode(node.node)
        }

        if (node.sign.kind == TokenType.MINUS) {
            if (nodeResult is Int) {
                return nodeResult * -1
            }
            else if (nodeResult is Double) {
                return nodeResult * -1.0
            }
        }
        return nodeResult
    }
}