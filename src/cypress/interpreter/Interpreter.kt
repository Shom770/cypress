package cypress.interpreter

import cypress.CypressError
import cypress.interpreter.types.*
import cypress.lexer.TokenType
import cypress.parser.Node

class Interpreter(private val sourceText: String) {
    private val globalSymbolTable = SymbolTable()

    fun <T> walk(node: Node, symbolTable: SymbolTable = globalSymbolTable): T {
        return when (node) {
            is Node.ArithmeticNode -> parseArithmeticNode(node, symbolTable)
            is Node.ConditionalNode -> parseConditionalNode(node, symbolTable)
            is Node.NumberNode -> parseNumberNode(node, symbolTable)
            is Node.StringNode -> parseStringNode(node, symbolTable)
            is Node.UnaryNode -> parseUnaryNode(node, symbolTable)
            is Node.VarAssignNode -> parseVarAssignNode(node, symbolTable)
            is Node.VarAccessNode -> parseVarAccessNode(node, symbolTable)
            is Node.MethodCallNode -> parseMethodCallNode(node, symbolTable)
            is Node.FunctionCallNode -> parseFunctionCallNode(node, symbolTable)
            is Node.IfNode -> parseIfNode(node, symbolTable)
            is Node.ProcedureNode -> parseProcedureNode(node, symbolTable)
            is Node.BlockNode -> parseBlockNode(node, SymbolTable(symbolTable))
            is Node.EmptyNode -> CypressNull(null) as T
            else -> throw CypressError.CypressTypeError("Can't process node $node")
        }
    }

    private inline fun <reified T : CypressNumber> parseArithmeticNode(
        node: Node.ArithmeticNode,
        symbolTable: SymbolTable
    ): T {
        val leftNode = walk<CypressNumber>(node.leftNode, symbolTable)
        val rightNode = walk<CypressNumber>(node.rightNode, symbolTable)

        return when (node.operator) {
            TokenType.PLUS -> leftNode + rightNode
            TokenType.MINUS -> leftNode - rightNode
            TokenType.ASTERISK -> leftNode * rightNode
            TokenType.FORWARD_SLASH -> leftNode / rightNode
            TokenType.DOUBLE_ASTERISK -> leftNode.pow(rightNode)
            else -> throw CypressError.CypressSyntaxError("Invalid token in expression : ${node.operator}")
        } as T
    }

    private inline fun <reified T : CypressNumber> parseConditionalNode(
        node: Node.ConditionalNode,
        symbolTable: SymbolTable
    ): T {
        val leftNode = walk<CypressType<Any>>(node.leftNode, symbolTable)
        val rightNode = walk<CypressType<Any>>(node.rightNode, symbolTable)

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

    private inline fun <reified T : CypressNumber> parseNumberNode(
        node: Node.NumberNode,
        symbolTable: SymbolTable
    ): T {
        return when (node.underlyingToken.kind) {
            TokenType.INT -> CypressInt(node.underlyingToken.text(sourceText))
            TokenType.FLOAT -> CypressDouble(node.underlyingToken.text(sourceText))
            else -> throw CypressError.CypressTypeError(
                "Underlying token for number node ${node.underlyingToken} is invalid."
            )
        } as T
    }

    private inline fun <reified T : CypressString> parseStringNode(
        node: Node.StringNode,
        symbolTable: SymbolTable
    ): T {
        return CypressString(node.underlyingToken.text(sourceText)) as T
    }

    private inline fun <reified T : CypressType<Any>> parseVarAssignNode(
        node: Node.VarAssignNode,
        symbolTable: SymbolTable
    ) : T {
        val nodeValue = walk<CypressType<Any>>(node.underlyingNode, symbolTable)
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
        val evaluatedParameters = node.parameters.map { walk<CypressType<Any>>(it, symbolTable) }

        return if (symbolTable.containsVariable(methodName)) {
            val procedure = symbolTable.lookupVariable(methodName) as CypressProcedure

            if (procedure.parameters.size != evaluatedParameters.size) {
                throw CypressError.CypressTypeError(
                    "Function $methodName expected ${procedure.parameters.size} parameters but got ${evaluatedParameters.size} parameters instead."
                )
            }

            val symbolTableForProcedure = SymbolTable(symbolTable)
            for ((i, parameterName) in procedure.parameters.withIndex()) {
                symbolTableForProcedure.setVariable(parameterName.text(sourceText), evaluatedParameters[i])
            }

            return parseBlockNode(procedure.value, symbolTableForProcedure)
        } else {
            (CypressBuiltIns
                .javaClass
                .methods
                .first { it.name == methodName }
                .invoke(CypressBuiltIns, *evaluatedParameters.toTypedArray()) ?: CypressNull(null)) as T
        }
    }

    private inline fun <reified T: CypressType<Any>> parseMethodCallNode(
        node: Node.MethodCallNode,
        symbolTable: SymbolTable
    ) : T {
        val targetValue = walk<CypressType<Any>>(node.methodTarget, symbolTable)
        val methodName = node.functionCallNode.functionNameNode.name.text(sourceText)
        val evaluatedParameters = node.functionCallNode.parameters.map { walk<CypressType<Any>>(it, symbolTable) }

        // Retrieve method name through the built-in Reflection API.
        val retrievedMethod = targetValue
            .javaClass
            .methods
            .first { it.name == methodName }

        return retrievedMethod.invoke(targetValue, *evaluatedParameters.toTypedArray()) as T
    }

    private inline fun <reified T: CypressProcedure> parseProcedureNode(
        node: Node.ProcedureNode,
        symbolTable: SymbolTable
    ) : T {
        val procedure = CypressProcedure(node.blockNode, node.parameterNames)
        symbolTable.setVariable(node.procedureName.text(sourceText), procedure as CypressType<Any>)
        return procedure as T
    }

    private inline fun <reified T: CypressType<Any>> parseIfNode(
        node: Node.IfNode,
        symbolTable: SymbolTable
    ) : T {
        for ((conditionalNode, blockNode) in node.conditionalNodes.zip(node.blockNodes)) {
            // Check if the conditional node passes as true (or CypressInt(1) in the language).
            if (walk<CypressType<Any>>(conditionalNode, symbolTable).asBoolean().value == 1) {
                return parseBlockNode(blockNode, symbolTable)
            }
        }

        if (node.blockNodes.size > node.conditionalNodes.size) {
            return parseBlockNode(node.blockNodes.last(), symbolTable)
        }

        return CypressInt(0) as T
    }

    private inline fun <reified T: CypressType<Any>> parseBlockNode(node: Node.BlockNode, symbolTable: SymbolTable) : T {
        val evaluatedNodes = node.nodes.map { walk<CypressType<*>>(it, symbolTable) }
        return evaluatedNodes.last() as T
    }

    private inline fun <reified T : CypressNumber> parseUnaryNode(node: Node.UnaryNode, symbolTable: SymbolTable) : T {
        return if (node.symbol == TokenType.MINUS) {
            (CypressInt(-1) * walk(node.underlyingNode, symbolTable)) as T
        } else if (node.symbol == TokenType.NOT) {
            walk<CypressInt>(node.underlyingNode, symbolTable).not() as T
        } else {
            walk(node.underlyingNode, symbolTable)
        }
    }
}
