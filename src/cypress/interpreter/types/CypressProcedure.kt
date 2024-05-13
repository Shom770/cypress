package cypress.interpreter.types

import cypress.CypressError
import cypress.lexer.Token
import cypress.parser.Node

class CypressProcedure(override val value: Node.BlockNode, val parameters: List<Token>) : CypressType<Node.BlockNode> {
    override fun compareTo(other: CypressType<Node.BlockNode>): CypressInt {
        throw CypressError.CypressTypeError("Functions cannot be compared to each other.")
    }

    override fun asBoolean(): CypressInt {
        throw CypressError.CypressTypeError("Functions cannot be evaluated as booleans.")
    }

}