package cypress.interpreter

import cypress.CypressError
import cypress.interpreter.types.*
import cypress.lexer.TokenType
import cypress.parser.Node

class Interpreter(private val sourceText: String) {
    private val globalSymbolTable = SymbolTable()

    fun <T> walk(node: Node, symbolTable: SymbolTable = globalSymbolTable): T {
        return when (node) {
            is Node.ArithmeticNode -> parseArithmeticNode(node)
            is Node.NumberNode -> parseNumberNode(node)
            is Node.VarAssignNode -> parseVarAssignNode(node, symbolTable)
            is Node.VarAccessNode -> parseVarAccessNode(node, symbolTable)
            else -> throw CypressError.CypressTypeError("Can't process node $node")
        }
    }

    private inline fun <reified T : CypressArithmeticTypes> parseArithmeticNode(node: Node.ArithmeticNode): T {
        val leftNode = walk<CypressNumber>(node.leftNode)
        val rightNode = walk<CypressNumber>(node.rightNode)

        return when (node.operator) {
            TokenType.PLUS -> leftNode + rightNode
            TokenType.MINUS -> leftNode - rightNode
            TokenType.ASTERISK -> leftNode * rightNode
            TokenType.FORWARD_SLASH -> leftNode / rightNode
            TokenType.DOUBLE_ASTERISK -> leftNode.pow(rightNode)
            else -> throw CypressError.CypressSyntaxError("Invalid token in expression : ${node.operator}")
        } as T
    }

    private inline fun <reified T : CypressNumber> parseNumberNode(node: Node.NumberNode): T {
        return when (node.underlyingToken.kind) {
            TokenType.INT -> CypressInt(node.underlyingToken.text(sourceText))
            TokenType.FLOAT -> CypressDouble(node.underlyingToken.text(sourceText))
            else -> throw CypressError.CypressTypeError(
                "Underlying token for number node ${node.underlyingToken} is invalid."
            )
        } as T
    }

    private inline fun <reified T : CypressType<Any>> parseVarAssignNode(
        node: Node.VarAssignNode,
        symbolTable: SymbolTable
    ) : T {
        val nodeValue = walk<CypressType<Any>>(node.underlyingNode)
        return symbolTable.setVariable(node.name.text(sourceText), nodeValue).let {
            nodeValue as T
        }
    }

    private inline fun <reified T: CypressType<Any>> parseVarAccessNode(
        node: Node.VarAccessNode,
        symbolTable: SymbolTable
    ) : T {
        return symbolTable.lookupVariable(node.name.text(sourceText)) as T
    }
}
