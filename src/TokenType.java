public enum TokenType {
    SEMICOLON("semi"),
    OR("or"),
    MINUS("minus"),
    COMMA("comma"),
    AND("and"),
    QUESTION_MARK("qmark"),
    PLUS("plus"),
    MULTIPLICATION("mult"),
    DIVISION("div"),
    NOT("not"),
    ASSIGN("assign"),
    OPEN_PARENTHESIS("openpar"),
    CLOSE_PARENTHESIS("closepar"),
    OPEN_SQUARE_BRACKET("opensqbr"),
    CLOSE_SQUARE_BRACKET("closesqbr"),
    OPEN_CURLY_BRACKET("opencubr"),
    CLOSE_CURLY_BRACKET("closecubr"),
    EQUAL("eq"),
    NOT_EQUAL("noteq"),
    GREATER_THAN("gt"),
    LESS_THAN("lt"),
    GREATER_EQUAL("geq"),
    LESS_EQUAL("leq"),
    COLON("colon"),
    COLON_COLON("coloncolon"),
    INLINE_COMMENT("inlinecmt"),
    BLOCK_COMMENT("blockcmt"),
    BREAK("break"),
    CLASS("class"),
    CONTINUE("continue"),
    ELSE("else"),
    FLOAT("float"),
    FUNC("func"),
    IF("if"),
    INTEGER("integer"),
    INHERITS("inherits"),
    ID("id"),
    STRING("stringlit");

    private String name;
    private TokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return name; }

}
