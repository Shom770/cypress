package cypress

sealed class Node {
    class BinOpNode(val leftNode: Node, val opToken: Token, val rightNode: Node): Node() {
        override fun toString(): String {
            return "BinOpNode(leftNode = $leftNode, opToken = $opToken, rightNode = $rightNode)"
        }
    }

    class NumberNode(val token: Token): Node() {
        override fun toString(): String {
            return "NumberNode(token = $token)"
        }
    }

    class UnaryOpNode(val sign: Token, val node: Node): Node() {
        override fun toString(): String {
            return "UnaryNode(sign = $sign, node = $node)"
        }
    }
}
