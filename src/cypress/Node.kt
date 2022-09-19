package cypress

sealed class Node {
    class BinOpNode(val leftNode: Node, val opToken: Token, val rightNode: Node) {
        override fun toString(): String {
            return "BinOpNode(leftNode = $leftNode, opToken = $opToken, rightNode = $rightNode)"
        }
    }

    class NumberNode(val value: String) {
        override fun toString(): String {
            return "NumberNode(value = $value)"
        }
    }

    class UnaryNode(val sign: String, val node: Node) {
        override fun toString(): String {
            return "UnaryNode(sign = $sign, node = $node)"
        }
    }
}
