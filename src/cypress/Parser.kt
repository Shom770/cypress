package cypress

class Parser(private val tokens: MutableList<Token>) {
    var position = -1
        set(value) {
            field = value
            currentToken = tokens.getOrNull(field)
        }
    var currentToken: Token? = null

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
        if (token!!.kind in setOf(TokenType.INT, TokenType.FLOAT)) {
            position += 1
            return Node.NumberNode(token)
        }
        else if (token.kind in setOf(TokenType.PLUS, TokenType.MINUS)) {
            position += 1
            return Node.UnaryOpNode(token, factor())
        }
        throw RuntimeException("Current token ($token) invalid.")
    }
}