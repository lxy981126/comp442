import java.util.ArrayList;

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
        SymbolTableRecord variableRecord = new SymbolTableRecord(node.table);
        variableRecord.setKind(SymbolKind.VARIABLE);
        node.record = variableRecord;
        iterateChildren(node);
        node.table.insert(variableRecord);
    }

    @Override
    protected void visitVariableDeclarationList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitStatementList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitMemberDeclaration(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.record = node.record;
            child.accept(this);

            if (child.type == ASTNodeType.VARIABLE_DECLARATION) {
                node.record = child.record;
                node.parent.record = node.record;
            }
            child = child.rightSibling;
        }
    }

    @Override
    public void visitFunctionDefinitionList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitFunctionDefinition(ASTNode node) {
        SymbolTableRecord funcHeadRecord = new SymbolTableRecord(node.table);
        funcHeadRecord.setKind(SymbolKind.FUNCTION);
        node.record = funcHeadRecord;

        SymbolTable funcBodyTable = new SymbolTable(node.table);
        funcHeadRecord.setLink(funcBodyTable);
        iterateWithLinkTable(node, funcBodyTable);

        node.table.insert(funcHeadRecord);
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
        link.name = node.record.getName();
        link.parent = node.table;
    }

    @Override
    protected void visitFunctionHead(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.ID) {
                FunctionType type = ((FunctionType) node.record.getType());
                type.scope = child.token.lexeme;
                child = child.rightSibling;
                continue;
            }
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }
    }

    @Override
    protected void visitFunctionBody(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitInherit(ASTNode node) {
        ClassType type = ((ClassType) node.record.getType());
        ASTNode child = node.leftmostChild;
        while (child != null) {
            type.parents.add(child.token.lexeme);
            child = child.rightSibling;
        }
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
        node.table.insert(node.record);
    }

    @Override
    protected void visitClassMethod(ASTNode node) { iterateChildren(node); }

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

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.FUNCTION_BODY) {
                SymbolTableRecord mainRecord = new SymbolTableRecord(node.table);
                mainRecord.setKind(SymbolKind.FUNCTION);
                mainRecord.setName("main");

                SymbolTable mainScope = new SymbolTable(node.table);
                mainScope.name = mainRecord.getName();

                child.table = mainScope;
                child.record = mainRecord;
                child.accept(this);

                mainRecord.setLink(mainScope);
                node.table.insert(mainRecord);
                child = child.rightSibling;
                continue;
            }
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }

        resolveFunctionScope(node);
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

    private void resolveFunctionScope(ASTNode programNode) {
        SymbolTable globalTable = programNode.table;
        ArrayList<SymbolTableRecord> records = new ArrayList<>(programNode.table.records.values());

        for (SymbolTableRecord record:records) {
            if (record.getKind() == SymbolKind.FUNCTION) {

                FunctionType type = ((FunctionType) record.getType());
                if (type.scope != null) {
                    SymbolTableRecord classRecord = globalTable.search(type.scope);
                    SymbolTable targetTable = classRecord.getLink();
                    targetTable.insert(record);
                    globalTable.delete(record.getName());
                }
            }
        }
    }
}
