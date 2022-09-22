package cypress

class Interpreter(private val nodes: List<Node>, private val text: String) {
    private val symbolTable = hashMapOf<String, CypressNumber>()

    fun walk(): MutableList<Any> {
        var result = mutableListOf<Any>()

        for (node in nodes) {
            result.add(
                when (node) {
                    is Node.BinOpNode -> parseNode(node)
                    is Node.IntNode -> parseNode(node)
                    is Node.DoubleNode -> parseNode(node)
                    is Node.UnaryOpNode -> parseNode(node)
                    is Node.VarAccessNode -> parseNode(node)
                    is Node.VarAssignNode -> parseNode(node)
                }
            )
        }

        return result
    }

    private fun parseNode(node: Node.VarAssignNode): CypressNumber {
        val nodeResult = when (node.node) {
            is Node.IntNode -> parseNode(node.node)
            is Node.DoubleNode -> parseNode(node.node)
            is Node.BinOpNode -> parseNode(node.node)
            is Node.UnaryOpNode -> parseNode(node.node)
            is Node.VarAssignNode -> parseNode(node.node)
            is Node.VarAccessNode -> parseNode(node.node)
        }

        symbolTable[node.identifier.text(text)] = nodeResult
        return nodeResult
    }

    private fun parseNode(node: Node.VarAccessNode): CypressNumber {
        return symbolTable[node.identifier.text(text)]
            ?: throw RuntimeException("Variable '${node.identifier.text(text)}' does not exist.")
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
            is Node.VarAssignNode -> parseNode(node.leftNode)
            is Node.VarAccessNode -> parseNode(node.leftNode)
        }
        val rightNodeResult = when (node.rightNode) {
            is Node.IntNode-> parseNode(node.rightNode)
            is Node.DoubleNode -> parseNode(node.rightNode)
            is Node.BinOpNode -> parseNode(node.rightNode)
            is Node.UnaryOpNode -> parseNode(node.rightNode)
            is Node.VarAssignNode -> parseNode(node.rightNode)
            is Node.VarAccessNode -> parseNode(node.rightNode)
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
            is Node.VarAssignNode -> parseNode(node.node)
            is Node.VarAccessNode -> parseNode(node.node)
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