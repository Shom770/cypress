package cypress

/**
 * A sealed class representing the different types of errors in Cypress.
 * @param message The error message passed into the error when thrown.
 */
sealed class CypressError(message: String) : Exception(message) {
    /**
     * A class deriving from Exception to represent a syntax error in Cypress.
     * @param message The error message passed into the syntax error when thrown.
     */
    class CypressSyntaxError(message: String) : CypressError(message)

    /**
     * A class deriving from Exception to represent a type error in Cypress.
     * @param message The error message passed into the syntax error when thrown.
     */
    class CypressTypeError(message: String) : CypressError(message)
}
