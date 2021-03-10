public enum SemanticSymbolType {
    // leaf
    NULL,
    NOT,
    REL_OP,
    ADD_OP,
    MULT_OP,
    NUM,
    ID,
    TYPE,
    SIGN,
    VISIBILITY,

    INDEX_LIST,
    FACTOR,
    TERM,
    ARITHMETIC_EXPRESSION,
    EXPRESSION,

    VARIABLE,
    VARIABLE_DECLARATION,
    VARIABLE_DECLARATION_LIST,

    STATEMENT_BLOCK,
    STATEMENT_LIST,
    ASSIGN_STATEMENT,
    IF_STATEMENT,
    WHILE_STATEMENT,
    READ_STATEMENT,
    WRITE_STATEMENT,
    RETURN_STATEMENT,
    BREAK_STATEMENT,
    CONTINUE_STATEMENT,
    FUNCTION_ASSIGN_STATEMENT,

    MEMBER_LIST,
    MEMBER_DECLARATION,

    ARRAY_SIZE_LIST,

    FUNCTION_DEFINITION_LIST,
    FUNCTION_DEFINITION,
    FUNCTION_DECLARATION,
    FUNCTION_BODY,
    INHERIT,

    CLASS_LIST,
    CLASS_DECLARATION,
    CLASS_DECLARATION_BODY,

    APARAM,
    APARAM_LIST,
    FPARAM,
    FPARAM_LIST
}
