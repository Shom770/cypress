package cypress

class Parser(
    private val tokens: MutableList<Token>,
    private val text: String,
    private val inIfStatement: Boolean = false
) {
    var position = -1
        set(value) {
            field = value
            currentToken = tokens.getOrNull(field)

            if (currentToken?.kind == TokenType.CLOSE_BRACE && inIfStatement) {
                currentToken = null
            }
        }
    var currentToken: Token? = null

    private fun peekAhead(): Token? {
        return tokens.getOrNull(position + 1)
    }

    fun parse(): MutableList<Node> {
        var nodes = mutableListOf<Node>()
        position += 1

        while (currentToken != null) {
            val result = expr()
            nodes.add(result)
        }

        return nodes
    }

    private fun expr(): Node {
        var result = term()

        while (
            currentToken != null
            && currentToken!!.kind in setOf(TokenType.PLUS, TokenType.MINUS, TokenType.DOUBLE_AMPERSAND)
        ) {
            val symbol = currentToken!!
            position += 1
            result = Node.BinOpNode(result, symbol, term())
        }

        return result
    }

    private fun term(): Node {
        var result = pow()

        while (
            currentToken != null
            && currentToken!!.kind in setOf(TokenType.ASTERISK, TokenType.FORWARD_SLASH, TokenType.DOUBLE_PIPE)
        ) {
            val symbol = currentToken!!
            position += 1
            result = Node.BinOpNode(result, symbol, pow())
        }

        return result
    }

    private fun pow(): Node {
        var result = factor()

        while (
            currentToken != null
            && currentToken!!.kind in setOf(
                TokenType.DOUBLE_ASTERISK,
                TokenType.GREATER_THAN,
                TokenType.LESS_THAN,
                TokenType.GREATER_THAN_OR_EQ,
                TokenType.LESS_THAN_OR_EQ,
                TokenType.EQUALS,
                TokenType.NOT_EQUALS
            )
        ) {
            val symbol = currentToken!!
            position += 1
            result = Node.BinOpNode(result, symbol, factor())
        }

        return result
    }

    private fun factor(): Node {
        val token = currentToken

        if (token!!.kind == TokenType.INT) {
            position += 1
            return Node.IntNode(CypressNumber.CypressInt(token.text(text).toInt()))
        }
        else if (token.kind == TokenType.FLOAT) {
            position += 1
            return Node.DoubleNode(CypressNumber.CypressDouble(token.text(text).toDouble()))
        }
        else if (token.kind in setOf(TokenType.PLUS, TokenType.MINUS)) {
            position += 1
            return Node.UnaryOpNode(token, factor())
        }
        else if (token.kind == TokenType.OPEN_PAREN) {
            position += 1
            return expr().also { position += 1 }
        }
        else if (token.kind == TokenType.IDENTIFIER) {
            return if (peekAhead()?.kind == TokenType.ASSIGN) {
                position += 2
                Node.VarAssignNode(token, expr())
            } else {
                position += 1
                Node.VarAccessNode(token)
            }
        }
        else if (token.kind == TokenType.IF) {
            return ifNode().also { position += 1 }
        }

        throw RuntimeException("Current token ($token) invalid.")
    }

    private fun ifNode(): Node {
        position += 1
        val logical_node = expr()

        if (currentToken!!.kind != TokenType.OPEN_BRACE) throw RuntimeException("If statement syntax invalid.")
        position += 1

        val body_nodes = parse().also { position += 1 }


    }
}