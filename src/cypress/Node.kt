package cypress

sealed class Node {
    data class ArithmeticNode(val leftNode: Node, val operator: TokenType, val rightNode: Node) : Node()
    data class ConditionalNode(val leftNode: Node, val compareOp: TokenType, val rightNode: Node) : Node()
    data class NumberNode(val underlyingToken: Token) : Node()
    data class UnaryNode(val symbol: TokenType, val underlyingNode: Node) : Node()
    data class VarAssignNode(val name: Token, val underlyingNode: Node) : Node()
    data class VarAccessNode(val name: Token) : Node()
}