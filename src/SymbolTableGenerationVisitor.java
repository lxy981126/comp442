public class SymbolTableGenerationVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        node.record.setName(node.token.lexeme);
    }

    @Override
    protected void visitType(ASTNode node) {
        SymbolType typeClass = node.record.getType();
        if (typeClass instanceof VariableType) {
            ((VariableType) typeClass).className = node.token.lexeme;
        }
        else {
            ((FunctionType) typeClass).returnType = new VariableType(node.token.lexeme);
        }
    }

    @Override
    protected void visitVisibility(ASTNode node) {
        if (node.token != null) {
            boolean isPrivate = node.token.lexeme == "private";
            node.record.getType().isPrivate = isPrivate;
        }
    }

    @Override
    protected void visitVariableDeclaration(ASTNode node) {
//        SymbolTableRecord record = new SymbolTableRecord()
    }

    @Override
    protected void visitMemberDeclaration(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }

    @Override
    protected void visitFunctionDeclaration(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }

    @Override
    protected void visitClassList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitClassDeclaration(ASTNode node) {
        SymbolTableRecord record = new SymbolTableRecord(node.table);
        record.setKind(SymbolKind.CLASS);

        SymbolTable linkTable = new SymbolTable(node.table);

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.record = record;
            child.table = linkTable;
            child.accept(this);
            child = child.rightSibling;
        }

        record.setLink(linkTable);
        node.record = record;
        node.table.insert(record);
        linkTable.name = record.getName();
        linkTable.parent = node.table;
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
        SymbolTableRecord record = new SymbolTableRecord(node.table);
        record.setKind(SymbolKind.FUNCTION);

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.record = record;
            child.accept(this);
            child = child.rightSibling;
        }

        node.table.insert(record);
    }

    @Override
    protected void visitProgram(ASTNode node) {
        node.table = new SymbolTable("Global", null);
        iterateChildren(node);
    }

    private void iterateChildren(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }
}
