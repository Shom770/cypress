package cypress.parser

import cypress.lexer.Token
import cypress.lexer.TokenType

sealed class Node {
    data class ArithmeticNode(val leftNode: Node, val operator: TokenType, val rightNode: Node) : Node()
    data class ConditionalNode(val leftNode: Node, val compareOp: TokenType, val rightNode: Node) : Node()
    data class NumberNode(val underlyingToken: Token) : Node()
    data class StringNode(val underlyingToken: Token) : Node()
    data class UnaryNode(val symbol: TokenType, val underlyingNode: Node) : Node()
    data class VarAssignNode(val name: Token, val underlyingNode: Node) : Node()
    data class VarAccessNode(val name: Token) : Node()
    data class FunctionCallNode(val functionNameNode: Node.VarAccessNode, val parameters: List<Node>) : Node()
    data class MethodCallNode(val methodTarget: Node, val functionCallNode: FunctionCallNode) : Node()
    data class BlockNode(val nodes: List<Node>) : Node()  // Signifies a block of code (marked by braces}
    data class ProcedureNode(val procedureName: Token, val parameterNames: List<Token>, val blockNode: BlockNode) : Node()
    object EmptyNode : Node()  // Used to signify EOFs
}