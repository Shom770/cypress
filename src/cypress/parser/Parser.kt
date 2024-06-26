package cypress.parser

import cypress.CypressError
import cypress.lexer.Token
import cypress.lexer.TokenType

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

    fun parseExpr(untilTokenTypes: List<TokenType>? = null): List<Node> {
        val nodes = mutableListOf<Node>()

        while (tokenIterator.hasNext()) {
            if (peekAhead().kind == TokenType.NEWLINE) {
                advance(1)
            }

            if (peekAhead().kind in (untilTokenTypes?.toTypedArray() ?: arrayOf())) {
                advance()
                break
            }
            else if (peekAhead().kind == TokenType.EOF) {
                break
            }

            nodes.add(parseWithBindingPower(0, untilTokenTypes))
        }

        return nodes
    }

    private fun parseWithBindingPower(bindingPower: Int, untilTokenTypes: List<TokenType>? = null): Node {
        // Advance across empty newlines
        while (peekAhead().kind == TokenType.NEWLINE) {
            advance()
        }

        if (peekAhead().kind == TokenType.EOF) {
            return Node.EmptyNode
        }

        var lhs: Node = matchNode(advance())

        // Short hand parsing to avoid trying to parse a right-hand side for an if statement
        if (lhs is Node.IfNode) {
            return lhs
        }

        var nextToken = peekAhead()

        while (
            nextToken.kind.precedence >= bindingPower
            && nextToken.kind !in hashSetOf(TokenType.NEWLINE, TokenType.EOF, *untilTokenTypes?.toTypedArray() ?: arrayOf())
        ) {
            advance()

            if (nextToken.kind in TokenType.conditionalTokens) {
               lhs = Node.ConditionalNode(lhs, nextToken.kind, parseWithBindingPower(bindingPower + 1, untilTokenTypes))
            } else if (nextToken.kind == TokenType.DOT) {
                lhs = Node.MethodCallNode(lhs, parseFunctionCall())
            }
            else if (untilTokenTypes != null && nextToken.kind !in untilTokenTypes || untilTokenTypes == null) {
                lhs = Node.ArithmeticNode(lhs, nextToken.kind, parseWithBindingPower(bindingPower + 1, untilTokenTypes))
            }

            nextToken = peekAhead()
        }

        if (untilTokenTypes != null && nextToken.kind in untilTokenTypes) {
            advance()
        }

        return lhs
    }

    private fun matchNode(token: Token): Node {
        return when (token.kind) {
            in hashSetOf(TokenType.INT, TokenType.FLOAT) -> Node.NumberNode(token)
            TokenType.STRING -> Node.StringNode(token)
            // 100 used as a sentinel value to only parse numbers
            in hashSetOf(TokenType.PLUS, TokenType.MINUS) -> Node.UnaryNode(token.kind, parseWithBindingPower(100))
            TokenType.IDENTIFIER -> parseIdentifier(token)
            TokenType.PROC -> parseFunctionCreation()
            TokenType.NOT -> Node.UnaryNode(token.kind, parseWithBindingPower(0))
            TokenType.OPEN_PAREN -> parseWithBindingPower(0, listOf(TokenType.CLOSE_PAREN))
            TokenType.OPEN_BRACE -> Node.BlockNode(parseExpr(listOf(TokenType.CLOSE_BRACE)))
            TokenType.IF -> parseIfStatement()
            else -> throw CypressError.CypressSyntaxError("Invalid usage of token: $token")
        }
    }

    private fun parseFunctionCreation(): Node {
        val functionName = advance()
        val parameters = mutableListOf<Token>()

        // Advance through the open parentheses
        if (peekAhead().kind == TokenType.OPEN_PAREN) {
            advance()
        } else {
            throw CypressError.CypressSyntaxError("Looks like you're missing a open parentheses when creating a procedure.")
        }

        if (peekAhead().kind != TokenType.CLOSE_PAREN) {
            while (peekAhead().kind !in hashSetOf(
                    TokenType.NEWLINE,
                    TokenType.EOF,
                    TokenType.CLOSE_PAREN,
                    TokenType.OPEN_BRACE
                )
            ) {
                val parameterName = advance()
                parameters.add(parameterName)

                if (peekAhead().kind != TokenType.COMMA && peekAhead().kind != TokenType.CLOSE_PAREN) {
                    throw CypressError.CypressSyntaxError("You're missing a comma or closing parentheses after a parameter in your procedure.")
                }

                advance()  // Advance past the comma
            }
        }
        else {
            advance()  // Advance past the open parentheses
        }

        advance()  // Advance past the closing parentheses

        if (tokens[tokenIterator.nextIndex() - 1].kind != TokenType.OPEN_BRACE) {
            throw CypressError.CypressSyntaxError("You're missing an open brace to open your procedure body.")
        }

        val blockNode = Node.BlockNode(parseExpr(listOf(TokenType.CLOSE_BRACE)))
        return Node.ProcedureNode(functionName, parameters, blockNode)
    }

    private fun parseIfStatement(): Node {
        val conditionalNodes = mutableListOf<Node.ConditionalNode>()
        val blockNodes = mutableListOf<Node.BlockNode>()

        while (
            tokens[tokenIterator.nextIndex() - 1].kind in hashSetOf(TokenType.IF, TokenType.ELIF, TokenType.ELSE)
        ) {
            advance()  // Advance past the if/elif/else token

            // Determines the conditional node for if/elif statements (but not else, because when you advance you'll land on an open brace)
            val inIfStatement = tokens[tokenIterator.previousIndex()].kind == TokenType.OPEN_PAREN
            if (inIfStatement) {
                val conditionalNode = parseWithBindingPower(0, listOf(TokenType.CLOSE_PAREN, TokenType.OPEN_BRACE))
                conditionalNodes.add(conditionalNode as Node.ConditionalNode)

                if (tokens[tokenIterator.nextIndex() - 1].kind == TokenType.CLOSE_PAREN) {
                    advance()
                }
            }

            val blockNode = Node.BlockNode(parseExpr(listOf(TokenType.CLOSE_BRACE)))
            blockNodes.add(blockNode)

            while (peekAhead().kind == TokenType.NEWLINE) {
                advance() // Advance onto the last newline
            }

            if (peekAhead().kind != TokenType.EOF && inIfStatement) {
                advance()  // Advance to the last newline
            }
        }

        return Node.IfNode(conditionalNodes.toList(), blockNodes.toList())
    }

    private fun parseIdentifier(token: Token): Node {
        return if (peekAhead().kind == TokenType.ASSIGN) {
            advance()
            Node.VarAssignNode(token, parseWithBindingPower(0))
        }
        else if (peekAhead().kind == TokenType.OPEN_PAREN) {
            parseFunctionCall(token)
        }
        else {
            Node.VarAccessNode(token)
        }
    }

    // Used only for parsing function calls so that recursion doesn't cause function calls to be parsed wrong.
    private fun parseRawIdentifier(token: Token): Node {
        return if (peekAhead().kind == TokenType.ASSIGN) {
            advance()
            Node.VarAssignNode(token, parseWithBindingPower(0))
        }
        else {
            Node.VarAccessNode(token)
        }
    }

    private fun parseFunctionCall(functionName: Token? = null): Node.FunctionCallNode {
        // Assume that the parser is on the dot token and advance
        val functionName = if (tokens[tokenIterator.nextIndex() - 1].kind == TokenType.DOT) {
            parseRawIdentifier(advance())
        } else if (functionName == null){
            parseRawIdentifier(tokens[tokenIterator.nextIndex() - 1])
        } else {
            Node.VarAccessNode(functionName)
        }
        val parameters = mutableListOf<Node>()

        // Advance through the open parentheses
        if (peekAhead().kind == TokenType.OPEN_PAREN) {
            advance()
        } else {
            throw CypressError.CypressSyntaxError("Looks like you're missing a open parentheses when calling a function.")
        }

        while (
            tokens[tokenIterator.nextIndex() - 1].kind !in hashSetOf(TokenType.NEWLINE, TokenType.EOF, TokenType.CLOSE_PAREN)
            && peekAhead().kind !in hashSetOf(TokenType.NEWLINE, TokenType.EOF, TokenType.CLOSE_PAREN)
        ) {
            val parameterNode = parseWithBindingPower(0, listOf(TokenType.COMMA, TokenType.CLOSE_PAREN))
            parameters.add(parameterNode)
        }

        // Consume the rest of the useless closing parentheses
        while (peekAhead().kind == TokenType.CLOSE_PAREN) {
            advance()
        }

        return Node.FunctionCallNode(functionName as Node.VarAccessNode, parameters.toList())
    }
}