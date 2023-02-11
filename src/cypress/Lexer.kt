package cypress


class Lexer(private val text: String) {
    private val tokens = mutableListOf<Token>()
    private val textIterator = text.toList().listIterator()

    private val validIdentifiers = ('A'..'z').joinToString("").replace("[\\]^_`", "_")
    private val validOperators = "+-*/="

    private fun advance(n: Int = 1): IndexedValue<Char> {
        repeat(n - 1) { textIterator.next() }
        return IndexedValue(textIterator.nextIndex(), textIterator.next())
    }

    private fun peekAhead(): Char? {
        return text.getOrNull(textIterator.nextIndex())
    }

    fun tokenize(): MutableList<Token> {
        while (textIterator.hasNext()) {
            val (index, currentChar) = advance()
            val nextChar = peekAhead()
            println("$index $currentChar ----- $nextChar")
//            when (currentChar) {
//                in validOperators -> parseOperator(index, currentChar)
//                in validIdentifiers -> parseIdentifier(index, currentChar)
//            }
        }

        return tokens
    }


}
