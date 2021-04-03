import java.io.IOException;

public class SemanticCheckingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        node.record = node.table.search(node.token.lexeme);
        if (node.record == null) {
            outputError("Use of undeclared variable: " + node.token.lexeme + "(line " + node.token.location + ")\n");
        }
        else {
            node.record.setLocation(node.token.location);
        }
    }

    protected void visitString(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        node.record.setKind(SymbolKind.VARIABLE);
        ((VariableType) node.record.getType()).className = "string";
        node.record.setLocation(node.token.location);
    }

    @Override
    protected void visitNum(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        node.record.setKind(SymbolKind.VARIABLE);
        node.record.setLocation(node.token.location);

        if (node.token.getType() == TokenType.INTEGER_NUMBER) {
            ((VariableType) node.record.getType()).className = "int";
        }
        else if (node.token.getType() == TokenType.FLOAT_NUMBER) {
            ((VariableType) node.record.getType()).className = "float";
        }
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
        SymbolTableRecord lhs = null;
        SymbolTableRecord rhs = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);

            if (child.type == ASTNodeType.ID) {
                lhs = child.record;
            }
            if (child.type == ASTNodeType.EXPRESSION) {
                rhs = child.record;
            }
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            checkType(lhs.getName(), rhs.getName(), lhs.getType(), rhs.getType(), lhs.getLocation(), rhs.getLocation());
        }
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        SymbolTableRecord returnRecord = node.record;
        SymbolTableRecord givenRecord = null;
        FunctionType returnType = ((FunctionType) returnRecord.getType());

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            givenRecord = child.record;
            child = child.rightSibling;
        }
        if (returnRecord != null && givenRecord != null) {
            checkType(returnRecord.getName(), givenRecord.getName(), returnType.returnType, givenRecord.getType(),
                    returnRecord.getLocation(), givenRecord.getLocation());
        }
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

    private void checkType(String name1, String name2, SymbolType type1, SymbolType type2, int location1, int location2) {
        String errorMessage = "Mismatch type: " + name1 + "(line " + location1 + "), " +
                name2 + "(line " + location2 + ")\n";

        if (!type1.equals(type2)) {
            outputError(errorMessage);
        }
    }

    private void outputError(String message) {
        System.err.print(message);
        try {
            errorWriter.write(message);
            errorWriter.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
