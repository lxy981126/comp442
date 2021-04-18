public class MemorySizeComputingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        SymbolTableRecord record = node.table.globalSearch(node.token.lexeme);
        if (record.getKind() == SymbolKind.VARIABLE || record.getKind() == SymbolKind.PARAMETER) {
            VariableType variableType = (VariableType) record.getType();
            record.setSize(computeVariableSize(variableType));
        }
    }

    @Override
    protected void visitFunctionParameterList(ASTNode node) {
        iterateWithGivenTable(node, node.table);
    }

    @Override
    protected void visitFunctionParameter(ASTNode node) {
        iterateWithGivenTable(node, node.table);
    }

    @Override
    protected void visitVariableDeclaration(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitVariableDeclarationList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        String functionName = node.table.name;
        SymbolTableRecord functionRecord = node.table.globalSearch(functionName);
        FunctionType functionType = ((FunctionType) functionRecord.getType());

        String returnName = node.table.parent.name + "_" + functionName + "_return";
        node.record = new SymbolTableRecord(node.table);
        node.record.setKind(SymbolKind.VARIABLE);
        node.record.setName(returnName);
        node.record.setType(functionType.returnType);
        node.record.setSize(computeVariableSize(functionType.returnType));
        node.table.insert(node.record);
    }

    @Override
    protected void visitStatementList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitMethodBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDefinitionList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDefinition(ASTNode node) {
        if (((FunctionType) node.record.getType()).scope.equals("Global")) {
            iterateChildren(node);
            collectSizeInScope(node.table, node.record.getName());
        }
    }

    @Override
    protected void visitFunctionHead(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitClassList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitClassDeclaration(ASTNode node) {
        iterateChildren(node);
        collectSizeInScope(node.table, node.record.getName());
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitMemberDeclaration(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDeclaration(ASTNode node) {
        iterateWithGivenTable(node, node.record.getLink());
        collectSizeInScope(node.table, node.record.getName());
    }

    @Override
    protected void visitProgram(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            if (child.type == ASTNodeType.FUNCTION_BODY) {
                collectSizeInScope(node.table, "main");
            }
            child = child.rightSibling;
        }
    }

    private void collectSizeInScope(SymbolTable table, String scopeName) {
        SymbolTableRecord scopeRecord = table.globalSearch(scopeName);
        int totalSize = scopeRecord.getSize();

        for (SymbolTableRecord record:scopeRecord.getLink().records) {
            totalSize += record.getSize();
        }
        scopeRecord.setSize(totalSize);
    }

    private void iterateChildren(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            child = child.rightSibling;
        }
    }

    private void iterateWithGivenTable(ASTNode node, SymbolTable table) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = table;
            child.accept(this);
            child = child.rightSibling;
        }
    }

    private int computeVariableSize(VariableType variableType) {
        int size = 0;
        if (variableType.className.equals("integer")) {
            size = 4;
        }
        else if (variableType.className.equals("float")){
            size = 8;
        }
        else {
            // todo: custom class type
        }

        if (variableType.dimension.size() != 0) {
            int dimension = 1;
            for (Integer i: variableType.dimension) {
                i = i==null? 16:i;
                dimension *= i;
            }
            size *= dimension;
        }
        return size;
    }
}
