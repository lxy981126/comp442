import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Visitor {
    protected static BufferedWriter errorWriter;

    Visitor() {}

    Visitor(String errorFile) throws IOException {
        if(errorWriter == null) {
            errorWriter = new BufferedWriter(new FileWriter(errorFile));
        }
    }

    public void visit(ASTNode node) {
        switch (node.type) {
            case NOT -> visitNot(node);
            case ASSIGN_OP -> visitAssignOperation(node);
            case REL_OP -> visitRelOperation(node);
            case ADD_OP -> visitAddOperation(node);
            case MULT_OP -> visitMultOperation(node);
            case NUM -> visitNum(node);
            case STRING -> visitString(node);
            case ID -> visitId(node);
            case TYPE -> visitType(node);
            case SIGN -> visitSign(node);
            case VISIBILITY -> visitVisibility(node);
            case INDEX_LIST -> visitIndexList(node);
            case FACTOR -> visitFactor(node);
            case FUNCTION_OR_VARIABLE -> visitFunctionOrVariable(node);
            case TERM -> visitTerm(node);
            case ARITHMETIC_EXPRESSION -> visitArithmeticExpression(node);
            case EXPRESSION -> visitExpression(node);
            case VARIABLE -> visitVariable(node);
            case VARIABLE_DECLARATION -> visitVariableDeclaration(node);
            case VARIABLE_DECLARATION_LIST -> visitVariableDeclarationList(node);
            case STATEMENT_BLOCK -> visitStatementBlock(node);
            case STATEMENT_LIST -> visitStatementList(node);
            case ASSIGN_STATEMENT -> visitAssignStatement(node);
            case IF_STATEMENT -> visitIfStatement(node);
            case WHILE_STATEMENT -> visitWhileStatement(node);
            case READ_STATEMENT -> visitReadStatement(node);
            case WRITE_STATEMENT -> visitWriteStatement(node);
            case RETURN_STATEMENT -> visitReturnStatement(node);
            case BREAK_STATEMENT -> visitBreakStatement(node);
            case CONTINUE_STATEMENT -> visitContinueStatement(node);
            case MEMBER_DECLARATION -> visitMemberDeclaration(node);
            case ARRAY_SIZE_LIST -> visitArraySizeList(node);
            case FUNCTION_DEFINITION_LIST -> visitFunctionDefinitionList(node);
            case FUNCTION_DEFINITION -> visitFunctionDefinition(node);
            case FUNCTION_DECLARATION -> visitFunctionDeclaration(node);
            case FUNCTION_HEAD -> visitFunctionHead(node);
            case FUNCTION_BODY -> visitFunctionBody(node);
            case INHERIT -> visitInherit(node);
            case CLASS_LIST -> visitClassList(node);
            case CLASS_DECLARATION -> visitClassDeclaration(node);
            case CLASS_DECLARATION_BODY -> visitClassDeclarationBody(node);
            case CLASS_METHOD -> visitClassMethod(node);
            case METHOD_BODY -> visitMethodBody(node);
            case APARAM -> visitArrayParameter(node);
            case APARAM_LIST -> visitArrayParameterList(node);
            case FPARAM -> visitFunctionParameter(node);
            case FPARAM_LIST -> visitFunctionParameterList(node);
            case PROGRAM -> visitProgram(node);
        }
    }

    protected void visitNot(ASTNode node) {}
    protected void visitAssignOperation(ASTNode node) {}
    protected void visitRelOperation(ASTNode node) {}
    protected void visitAddOperation(ASTNode node) {}
    protected void visitMultOperation(ASTNode node) {}
    protected void visitNum(ASTNode node) {}
    protected void visitString(ASTNode node) {}
    protected void visitId(ASTNode node) {}
    protected void visitType(ASTNode node) {}
    protected void visitSign(ASTNode node) {}
    protected void visitVisibility(ASTNode node) {}
    protected void visitIndexList(ASTNode node) {}
    protected void visitFactor(ASTNode node) {}
    protected void visitFunctionOrVariable(ASTNode node) {}
    protected void visitTerm(ASTNode node) {}
    protected void visitArithmeticExpression(ASTNode node) {}
    protected void visitExpression(ASTNode node) {}
    protected void visitVariable(ASTNode node) {}
    protected void visitVariableDeclaration(ASTNode node) {}
    protected void visitVariableDeclarationList(ASTNode node) {}
    protected void visitStatementBlock(ASTNode node) {}
    protected void visitStatementList(ASTNode node) {}
    protected void visitAssignStatement(ASTNode node) {}
    protected void visitIfStatement(ASTNode node) {}
    protected void visitWhileStatement(ASTNode node) {}
    protected void visitReadStatement(ASTNode node) {}
    protected void visitWriteStatement(ASTNode node) {}
    protected void visitReturnStatement(ASTNode node) {}
    protected void visitBreakStatement(ASTNode node) {}
    protected void visitContinueStatement(ASTNode node) {}
    protected void visitMemberDeclaration(ASTNode node) {}
    protected void visitArraySizeList(ASTNode node) {}
    protected void visitFunctionDefinitionList(ASTNode node) {}
    protected void visitFunctionDefinition(ASTNode node) {}
    protected void visitFunctionDeclaration(ASTNode node) {}
    protected void visitFunctionHead(ASTNode node) {}
    protected void visitFunctionBody(ASTNode node) {}
    protected void visitInherit(ASTNode node) {}
    protected void visitClassList(ASTNode node) {}
    protected void visitClassDeclaration(ASTNode node) {}
    protected void visitClassDeclarationBody(ASTNode node) {}
    protected void visitClassMethod(ASTNode node) {}
    protected void visitMethodBody(ASTNode node) {}
    protected void visitArrayParameterList(ASTNode node) {}
    protected void visitArrayParameter(ASTNode node) {}
    protected void visitFunctionParameterList(ASTNode node) {}
    protected void visitFunctionParameter(ASTNode node) {}
    protected void visitProgram(ASTNode node) {}
}
