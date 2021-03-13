public enum ASTNodeType {
    // leaf
    NULL("null"),
    NOT("not"),
    ASSIGN_OP("assginOp"),
    REL_OP("relOp"),
    ADD_OP("addOp"),
    MULT_OP("multOp"),
    NUM("num"),
    STRING("str"),
    ID("id"),
    TYPE("type"),
    SIGN("sign"),
    PRIVATE("private"),
    PUBLIC("public"),

    VISIBILITY("visibility"),
    INDEX_LIST("indexList"),
    FACTOR("factor"),
    FUNCTION_OR_VARIABLE("funcOrVar"),
    TERM("term"),
    ARITHMETIC_EXPRESSION("arithExpr"),
    EXPRESSION("expr"),

    VARIABLE("var"),
    VARIABLE_DECLARATION_LIST("varDeclList"),
    VARIABLE_DECLARATION("varDecl"),

    STATEMENT_BLOCK("statBlock"),
    STATEMENT_LIST("statList"),
    ASSIGN_STATEMENT("assignStat"),
    IF_STATEMENT("ifStat"),
    WHILE_STATEMENT("whileStat"),
    READ_STATEMENT("readStat"),
    WRITE_STATEMENT("writeStat"),
    RETURN_STATEMENT("returnStat"),
    BREAK_STATEMENT("breakStat"),
    CONTINUE_STATEMENT("continueStat"),

    MEMBER_DECLARATION("membDecl"),
    ARRAY_SIZE_LIST("arraySizeList"),

    FUNCTION_DEFINITION_LIST("funcDefList"),
    FUNCTION_DECLARATION("funcDecl"),
    FUNCTION_DEFINITION("funcDef"),
    FUNCTION_HEAD("funcHead"),
    FUNCTION_BODY("funcBody"),
    INHERIT("inherit"),

    CLASS_LIST("classList"),
    CLASS_DECLARATION("classDecl"),
    CLASS_DECLARATION_BODY("classDeclBody"),
    CLASS_METHOD("classMethod"),
    METHOD_BODY("methodBody"),

    APARAM("aParam"),
    APARAM_LIST("aParamList"),
    FPARAM("fParam"),
    FPARAM_LIST("fParamList"),
    PROGRAM("program");

    private String name;
    ASTNodeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return name; }
}
