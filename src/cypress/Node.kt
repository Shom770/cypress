package cypress

sealed class Node {
    data class ArithmeticNode(val leftNode: Node, val symbol: TokenType, val rightNode: Node) : Node()
    data class NumberNode(val underlyingToken: Token) : Node()
}