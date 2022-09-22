package cypress

sealed class Node {
    class BinOpNode(val leftNode: Node, val opToken: Token, val rightNode: Node): Node() {
        override fun toString(): String {
            return "BinOpNode(leftNode = $leftNode, opToken = $opToken, rightNode = $rightNode)"
        }
    }

    class IntNode(val value: CypressNumber.CypressInt): Node() {
        override fun toString(): String {
            return "IntNode(value = $value)"
        }
    }

    class DoubleNode(val value: CypressNumber.CypressDouble): Node() {
        override fun toString(): String {
            return "DoubleNode(value = $value)"
        }
    }

    class UnaryOpNode(val sign: Token, val node: Node): Node() {
        override fun toString(): String {
            return "UnaryNode(sign = $sign, node = $node)"
        }
    }
}
