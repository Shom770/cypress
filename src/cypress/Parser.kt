package cypress

class Parser(private val tokens: MutableList<Token>) {
    private val tokenIterator = tokens.listIterator()

    private fun advance(n: Int = 1): Token {
        // Deal with the case where when advancing through the text, the amount of times to advance is 0.
        if (n <= 0) {
            return tokens[tokenIterator.nextIndex() - 1]
        }

        repeat(n - 1) { tokenIterator.next() }

        return tokenIterator.next()
    }

    private fun peekAhead(): Token {
        // The span of TokenType.EOF doesn't matter here, therefore we are using a sentinel value.
        return tokens.getOrNull(tokenIterator.nextIndex()) ?: Token(TokenType.EOF, span = 0..0)
    }

    fun parseExpr(): List<Node> {
        val nodes = mutableListOf<Node>()

        while (tokenIterator.hasNext()) {
            if (peekAhead().kind == TokenType.NEWLINE) {
                advance(2)
            }
            nodes.add(parseWithBindingPower(0))
        }

        return nodes
    }

    private fun parseWithBindingPower(bindingPower: Int): Node {
        var lhs: Node = matchNode(advance())
        var nextToken = peekAhead()

        while (
            nextToken.kind.precedence >= bindingPower
            && nextToken.kind !in setOf(TokenType.NEWLINE, TokenType.EOF)
        ) {
            advance()
            val rhs = parseWithBindingPower(bindingPower + 1)

            lhs = Node.ArithmeticNode(lhs, nextToken.kind, rhs)
            nextToken = peekAhead()
        }

        return lhs
    }

    private fun matchNode(token: Token): Node {
        return when (token.kind) {
            in setOf(TokenType.INT, TokenType.FLOAT) -> Node.NumberNode(token)
            in setOf(TokenType.PLUS, TokenType.MINUS) -> Node.UnaryNode(token.kind, advance())
            else -> throw CypressError.CypressSyntaxError("Invalid usage of token: $token")
        }
    }
}