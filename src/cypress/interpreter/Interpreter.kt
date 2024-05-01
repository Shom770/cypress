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
            is Node.ConditionalNode -> parseConditionalNode(node)
            is Node.NumberNode -> parseNumberNode(node)
            is Node.StringNode -> parseStringNode(node)
            is Node.UnaryNode -> parseUnaryNode(node)
            is Node.VarAssignNode -> parseVarAssignNode(node, symbolTable)
            is Node.VarAccessNode -> parseVarAccessNode(node, symbolTable)
            is Node.MethodCallNode -> parseMethodCallNode(node)
            is Node.FunctionCallNode -> parseFunctionCallNode(node, symbolTable)
            is Node.EmptyNode -> CypressNull(null) as T
            else -> throw CypressError.CypressTypeError("Can't process node $node")
        }
    }

    private inline fun <reified T : CypressNumber> parseArithmeticNode(node: Node.ArithmeticNode): T {
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

    private inline fun <reified T : CypressNumber> parseConditionalNode(node: Node.ConditionalNode): T {
        val leftNode = walk<CypressType<Any>>(node.leftNode)
        val rightNode = walk<CypressType<Any>>(node.rightNode)

        return when (node.compareOp) {
            TokenType.EQUALS -> leftNode.equals(rightNode)
            TokenType.LESS_THAN -> CypressInt(leftNode.compareTo(rightNode).value < 0)
            TokenType.LESS_THAN_OR_EQUAL -> CypressInt(leftNode.compareTo(rightNode).value <= 0)
            TokenType.GREATER_THAN -> CypressInt(leftNode.compareTo(rightNode).value > 0)
            TokenType.GREATER_THAN_OR_EQUAL -> CypressInt(leftNode.compareTo(rightNode).value >= 0)
            TokenType.AND -> leftNode.asBoolean().and(rightNode.asBoolean())
            TokenType.OR -> leftNode.asBoolean().or(rightNode.asBoolean())
            else -> throw CypressError.CypressSyntaxError("Invalid token in expression : ${node.compareOp}")
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

    private inline fun <reified T : CypressString> parseStringNode(node: Node.StringNode): T {
        return CypressString(node.underlyingToken.text(sourceText)) as T
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

    private inline fun <reified T: CypressType<Any>> parseFunctionCallNode(
        node: Node.FunctionCallNode,
        symbolTable: SymbolTable
    ) : T {
        val methodName = node.functionNameNode.name.text(sourceText)
        val evaluatedParameters = node.parameters.map { walk<CypressType<Any>>(it) }

        return if (symbolTable.underlyingTable.containsKey(methodName)) {
            symbolTable.lookupVariable(methodName) as T  // TODO: Add implementation for this later
        } else {
            (CypressBuiltIns
                .javaClass
                .methods
                .first { it.name == methodName }
                .invoke(CypressBuiltIns, *evaluatedParameters.toTypedArray()) ?: CypressNull(null)) as T
        }
    }

    private inline fun <reified T: CypressType<Any>> parseMethodCallNode(node: Node.MethodCallNode) : T {
        val targetValue = walk<CypressType<Any>>(node.methodTarget)
        val methodName = node.functionCallNode.functionNameNode.name.text(sourceText)
        val evaluatedParameters = node.functionCallNode.parameters.map { walk<CypressType<Any>>(it) }

        // Retrieve method name through the built-in Reflection API.
        val retrievedMethod = targetValue
            .javaClass
            .methods
            .first { it.name == methodName }

        return retrievedMethod.invoke(targetValue, *evaluatedParameters.toTypedArray()) as T
    }

    private inline fun <reified T : CypressNumber> parseUnaryNode(node: Node.UnaryNode) : T {
        return if (node.symbol == TokenType.MINUS) {
            (CypressInt(-1) * walk(node.underlyingNode)) as T
        } else if (node.symbol == TokenType.NOT) {
            walk<CypressInt>(node.underlyingNode).not() as T
        } else {
            walk(node.underlyingNode)
        }
    }
}
