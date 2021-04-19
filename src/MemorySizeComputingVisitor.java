import java.util.ArrayList;

public class MemorySizeComputingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        SymbolTableRecord record = node.table.globalSearch(node.token.lexeme);
        if (record.getKind() == SymbolKind.VARIABLE || record.getKind() == SymbolKind.PARAMETER) {
            VariableType variableType = (VariableType) record.getType();
            record.setSize(computeVariableSize(node.table, variableType));
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
        node.record.setSize(computeVariableSize(node.table, functionType.returnType));
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
        if (node.record.getKind() == SymbolKind.FUNCTION) {

            String functionName = node.record.getName();
            FunctionType functionType = ((FunctionType) node.record.getType());

            String returnName = node.table.name + "_" + functionName + "_return";
            node.record = new SymbolTableRecord(node.table);
            node.record.setKind(SymbolKind.VARIABLE);
            node.record.setName(returnName);
            node.record.setType(functionType.returnType);
            node.record.setSize(computeVariableSize(node.table, functionType.returnType));
            node.table.insert(node.record);
        }
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
        ArrayList<ASTNode> children = getChildrenArraylist(node);
        for (int i = children.size() - 1; i >=0; i--) {
            ASTNode child = children.get(i);
            child.accept(this);
            if (child.type == ASTNodeType.FUNCTION_BODY) {
                collectSizeInScope(node.table, "main");
            }
        }
    }

    private void collectSizeInScope(SymbolTable table, String scopeName) {
        SymbolTableRecord scopeRecord = table.globalSearch(scopeName);
        int totalSize = scopeRecord.getSize();

        for (SymbolTableRecord record:scopeRecord.getLink().records) {
            totalSize += record.getKind()==SymbolKind.FUNCTION? 0:record.getSize();
        }
        scopeRecord.setSize(totalSize);
    }

    private void iterateChildren(ASTNode node) {
        ArrayList<ASTNode> children = getChildrenArraylist(node);
        for (int i = children.size() - 1; i >=0; i--) {
            children.get(i).accept(this);
        }
    }

    private void iterateWithGivenTable(ASTNode node, SymbolTable table) {
        ArrayList<ASTNode> children = getChildrenArraylist(node);
        for (int i = children.size() - 1; i >=0; i--) {
            children.get(i).table = table;
            children.get(i).accept(this);
        }
    }

    private ArrayList<ASTNode> getChildrenArraylist(ASTNode node) {
        ArrayList<ASTNode> list = new ArrayList<>();
        ASTNode child = node.leftmostChild;
        while (child != null) {
            list.add(child);
            child = child.rightSibling;
        }
        return list;
    }

    private int computeVariableSize(SymbolTable table, VariableType variableType) {
        int size = 0;
        if (variableType.className.equals("integer")) {
            size = 4;
        }
        else if (variableType.className.equals("float")){
            size = 8;
        }
        else {
            size = table.globalSearch(variableType.className).getSize();
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
