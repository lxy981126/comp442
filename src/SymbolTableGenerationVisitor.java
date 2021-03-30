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
        SymbolTableRecord variableRecord = node.record;
        variableRecord.setKind(SymbolKind.VARIABLE);
        node.record = variableRecord;
        iterateChildren(node);
        node.table.insert(variableRecord);
    }

    @Override
    protected void visitVariableDeclarationList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitMemberDeclaration(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitFunctionDefinition(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDeclaration(ASTNode node) {
        node.record.setKind(SymbolKind.FUNCTION);
        SymbolTable link = new SymbolTable(node.table);
        node.record.setLink(link);

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = link;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }

    @Override
    protected void visitFunctionHead(ASTNode node) {
//        SymbolTableRecord functionRecord = new SymbolTableRecord(node.table);
//        node.record = functionRecord;
//        iterateChildren(node);
//        node.table.insert(functionRecord);
    }

    @Override
    protected void visitFunctionBody(ASTNode node) {
        SymbolTableRecord functionBody = new SymbolTableRecord(node.table);
        functionBody.setKind(SymbolKind.FUNCTION);
        node.record = functionBody;

        SymbolTable linkTable = new SymbolTable(node.table);
        iterateWithLinkTable(node, linkTable);

        linkTable.name = functionBody.getName();
        linkTable.parent = node.table;
        node.table.insert(functionBody);
    }

    @Override
    protected void visitClassList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitClassDeclaration(ASTNode node) {
        SymbolTableRecord record = new SymbolTableRecord(node.table);
        record.setKind(SymbolKind.CLASS);
        node.record = record;

        SymbolTable linkTable = new SymbolTable(node.table);
        record.setLink(linkTable);
        iterateWithLinkTable(node,linkTable);

        linkTable.name = record.getName();
        linkTable.parent = node.table;
        node.table.insert(record);
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
        SymbolTableRecord record = new SymbolTableRecord(node.table);
        node.record = record;

        iterateChildren(node);
        node.table.insert(record);
    }

    @Override
    protected void visitMethodBody(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitFunctionParameter(ASTNode node) {
        SymbolTableRecord parameterRecord = new SymbolTableRecord(node.table);
        parameterRecord.setKind(SymbolKind.PARAMETER);
        node.record = parameterRecord;

        iterateChildren(node);
        node.table.insert(node.record);
    }

    @Override
    protected void visitFunctionParameterList(ASTNode node) {
        iterateChildren(node);
        FunctionType functionType = (FunctionType) node.record.getType();
        for (SymbolTableRecord record:node.record.getLink().records.values()) {
            functionType.parameters.add((VariableType) record.getType());
        }
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

    private void iterateWithLinkTable(ASTNode node, SymbolTable linkTable) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = linkTable;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }
}
