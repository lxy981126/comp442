import java.io.IOException;

public class SemanticCheckingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        node.record = node.table.search(node.token.lexeme);
    }

    @Override
    protected void visitNum(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        node.record.setKind(SymbolKind.VARIABLE);

        if (node.token.getType() == TokenType.INTEGER_NUMBER) {
            ((VariableType) node.record.getType()).className = "int";
        }
        else if (node.token.getType() == TokenType.FLOAT_NUMBER) {
            ((VariableType) node.record.getType()).className = "float";
        }

        node.parent.record = node.record;
    }

    @Override
    protected void visitFactor(ASTNode node) { iterateChildren(node); }
    @Override
    protected void visitTerm(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitArithmeticExpression(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitExpression(ASTNode node) { iterateChildren(node); }

    @Override
    public void visitAssignStatement(ASTNode node) {
        VariableType lhs = null;
        VariableType rhs = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);

            if (child.type == ASTNodeType.ID) {
                lhs = ((VariableType) child.record.getType());
            }

            if (child.type == ASTNodeType.EXPRESSION) {
                rhs = (VariableType) child.record.getType();
            }
            child = child.rightSibling;
        }
        checkType(lhs, rhs);
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        VariableType returnType = ((FunctionType) node.record.getType()).returnType;
        VariableType givenType = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            givenType = (VariableType) child.record.getType();
            child = child.rightSibling;
        }
        checkType(returnType, givenType);
    }

    @Override
    protected void visitStatementList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDefinition(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDefinitionList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitProgram(ASTNode node) {
        iterateChildren(node);
    }

    private void iterateChildren(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            node.record = child.record;
            child = child.rightSibling;
        }
    }

    private void checkType(SymbolType type1, SymbolType type2) {
        // todo: print error location
        String errorMessage = "mismatch return type";
        if (!type1.equals(type2)) {
            System.err.println(errorMessage);

            try {
                errorWriter.write(errorMessage);
                errorWriter.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
