package cypress

class Parser(private val tokens: MutableList<Token>, private val text: String) {
    var position = -1
        set(value) {
            field = value
            currentToken = tokens.getOrNull(field)
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

        while (currentToken != null && currentToken!!.kind in setOf(TokenType.PLUS, TokenType.MINUS)) {
            val symbol = currentToken!!
            position += 1
            result = Node.BinOpNode(result, symbol, term())
        }

        return result
    }

    private fun term(): Node {
        var result = pow()

        while (currentToken != null && currentToken!!.kind in setOf(TokenType.ASTERISK, TokenType.FORWARD_SLASH)) {
            val symbol = currentToken!!
            position += 1
            result = Node.BinOpNode(result, symbol, pow())
        }

        return result
    }

    private fun pow(): Node {
        var result = factor()

        while (currentToken != null && currentToken!!.kind == TokenType.DOUBLE_ASTERISK) {
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
            val result = expr()
            position += 1

            return result
        }
        else if (token.kind == TokenType.IDENTIFIER) {
            return if (peekAhead()?.kind == TokenType.ASSIGN) {
                position += 2
                Node.VarAssignNode(token, expr())
            } else {
                Node.VarAccessNode(token)
            }
        }

        throw RuntimeException("Current token ($token) invalid.")
    }
}