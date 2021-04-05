import java.io.IOException;
import java.util.ArrayList;

public class SymbolTableGenerationVisitor extends Visitor{

    SymbolTableGenerationVisitor(String errorFile) throws IOException { super(errorFile); }

    @Override
    protected void visitNum(ASTNode node) {
        ((VariableType) node.record.getType()).className = node.token.getType().toString();
    }

    @Override
    protected void visitId(ASTNode node) {
        node.record.setName(node.token.lexeme);
        node.record.setLocation(node.token.location);
    }

    @Override
    protected void visitString(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
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
    protected void visitIndexList(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitFactor(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitFunctionOrVariable(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitTerm(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitArithmeticExpression(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitExpression(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitVariable(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitVariableDeclaration(ASTNode node) {
        SymbolTableRecord variableRecord = new SymbolTableRecord(node.table);
        variableRecord.setKind(SymbolKind.VARIABLE);
        node.record = variableRecord;
        iterateChildren(node);
        boolean error = node.table.insert(variableRecord);
        if (error) {
            errorHandling(node);
        }
    }

    @Override
    protected void visitVariableDeclarationList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitStatementList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitStatementBlock(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitAssignStatement(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        collectLeafRecord(node);
    }

    @Override
    protected void visitArraySizeList(ASTNode node) {
        VariableType type = ((VariableType) node.record.getType());

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (!child.token.lexeme.contains("]")) {
                type.dimension.add(Integer.parseInt(child.token.lexeme));
            }
            else {
                type.dimension.add(null);
            }
            child = child.rightSibling;
        }
    }

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
                child.table.delete(child.record);
            }
            child = child.rightSibling;
        }
    }

    @Override
    public void visitFunctionDefinitionList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionDefinition(ASTNode node) {
        SymbolTableRecord funcHeadRecord = new SymbolTableRecord(node.table);
        funcHeadRecord.setKind(SymbolKind.FUNCTION);
        node.record = funcHeadRecord;

        SymbolTable funcBodyTable = new SymbolTable(node.table);
        funcHeadRecord.setLink(funcBodyTable);
        iterateWithLinkTable(node, funcBodyTable);

        boolean error = node.table.insert(funcHeadRecord);
        if (error) {
            errorHandling(node);
        }
        funcBodyTable.name = node.record.getName();
        funcBodyTable.parent = node.table;
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
            if (node.record.getName() != null && child.type == ASTNodeType.ID) {
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
    protected void visitFunctionBody(ASTNode node) {
        iterateChildren(node);
        ((FunctionType) node.record.getType()).hasDefinition = true;
    }

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
    protected void visitClassList(ASTNode node) {
        iterateChildren(node);
    }

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

        for (SymbolTableRecord linkRecord:linkTable.records) {
            if (linkRecord.getKind()==SymbolKind.FUNCTION) {
                ((FunctionType) linkRecord.getType()).scope = linkTable.name;
            }
        }

        boolean error = node.table.insert(record);
        if (error) {
            errorHandling(node);
        }
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
        SymbolTableRecord record = new SymbolTableRecord(node.table);
        node.record = record;

        iterateChildren(node);

        boolean error = node.table.insert(node.record);
        if (error) {
            errorHandling(node);
        }
    }

    @Override
    protected void visitClassMethod(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitMethodBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionParameter(ASTNode node) {
        SymbolTableRecord parameterRecord = new SymbolTableRecord(node.table);
        parameterRecord.setKind(SymbolKind.PARAMETER);
        node.record = parameterRecord;

        iterateChildren(node);
        boolean error = node.table.insert(node.record);
        if (error) {
            errorHandling(node);
        }
    }

    @Override
    protected void visitFunctionParameterList(ASTNode node) {
        iterateChildren(node);
        FunctionType functionType = (FunctionType) node.record.getType();
        for (SymbolTableRecord record:node.record.getLink().records) {
            if (record.getKind() == SymbolKind.PARAMETER){
                functionType.parameters.add((VariableType) record.getType());
            }
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
                boolean error = node.table.insert(mainRecord);
                if (error) {
                    errorHandling(child);
                }
                child = child.rightSibling;
                continue;
            }
            child.table = node.table;
            child.record = node.record;
            child.accept(this);
            child = child.rightSibling;
        }

        resolveFunctionScope(node);
        semanticChecking(node);
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

    private void collectLeafRecord(ASTNode parent) {
        ASTNode child = parent.leftmostChild;

        while (child != null) {
            if (child.leftmostChild == null) {
                if (child.type == ASTNodeType.ID) {
                    child.record = new SymbolTableRecord(parent.table);
                    child.record.setKind(SymbolKind.VARIABLE);
                }
            }
            child.table = parent.table;
            if (child.leftmostChild != null) {
                child.accept(this);
            }
            parent.record = child.record;
            child = child.rightSibling;
        }
    }

    private void resolveFunctionScope(ASTNode programNode) {
        SymbolTable globalTable = programNode.table;
        ArrayList<SymbolTableRecord> records = new ArrayList<>(programNode.table.records);

        for (SymbolTableRecord record:records) {
            if (record.getKind() == SymbolKind.FUNCTION) {

                FunctionType type = ((FunctionType) record.getType());
                if (type.scope != null) {
                    SymbolTableRecord classRecord = globalTable.search(type.scope);
                    SymbolTable classTable = classRecord.getLink();
                    SymbolTableRecord functionRecord = classTable.search(record.getName());

                    if (functionRecord != null) {
                        functionRecord.setLink(record.getLink());
                        functionRecord.setParent(classTable);
                        functionRecord.setType(record.getType());
                        functionRecord.getLink().parent = classTable;
                        globalTable.delete(record);
                    }
                    else {
                        String errorMessage = "Semantic Error - No declaration for declared member function at line " +
                                record.getLocation() + ": " + record.getName() + "\n";
                        errors.put(errorMessage, record.getLocation());
                    }
                }
                if (type.scope == null) {
                    type.scope = globalTable.name;
                }
            }
        }
    }

    private void semanticChecking(ASTNode programNode) {
        SymbolTable globalTable = programNode.table;
        for (SymbolTableRecord record:globalTable.records) {

            if (record.getKind() == SymbolKind.CLASS) {
                shadowedMember(record);
                circularClassDependency(record);
                memberFunctionDefinition(record);

                for (SymbolTableRecord classMember:record.getLink().records) {
                    if (classMember.getKind() == SymbolKind.FUNCTION) {
                        functionOverloading(classMember);
                    }
                }
            }
            else if (record.getKind() == SymbolKind.FUNCTION) {
                functionOverloading(record);
                for (SymbolTableRecord variableRecord:record.getLink().records) {
                    if (variableRecord.getKind() == SymbolKind.VARIABLE || variableRecord.getKind() == SymbolKind.PARAMETER) {
                        undeclaredClass(variableRecord);
                    }
                }
            }
        }
    }

    private void shadowedMember(SymbolTableRecord record) {
        SymbolTable classTable = record.getLink();
        ClassType classType = ((ClassType) record.getType());

        for (String parentClassName:classType.parents) {
            SymbolTable parentClassBody = record.getParent().search(parentClassName).getLink();
            for (SymbolTableRecord classMember:classTable.records) {

                SymbolTableRecord memberRecord = parentClassBody.search(classMember.getName());
                if (memberRecord != null && memberRecord.getKind() != SymbolKind.FUNCTION) {
                    String errorMessage = "Semantic Warning - Shadowed inherited member at line " +
                            classMember.getLocation() + ": " + classMember.getName() + "\n";
                    errors.put(errorMessage, classMember.getLocation());
                }
            }
        }
    }

    private void circularClassDependency(SymbolTableRecord record) {
        ClassType classType = ((ClassType) record.getType());

        for (String parentClassName:classType.parents) {
            SymbolTableRecord parentClassRecord = record.getParent().search(parentClassName);
            ClassType parentClassType = ((ClassType) parentClassRecord.getType());
            for (String parentInheritedClass: parentClassType.parents) {

                if (parentInheritedClass.equals(record.getName())) {
                    String errorMessage = "Semantic Error - Circular class dependency at line " +
                            record.getLocation() + ": " + record.getName() + "\n";
                    errors.put(errorMessage, record.getLocation());
                }
            }
        }
    }

    private void memberFunctionDefinition(SymbolTableRecord record) {
        SymbolTable classTable = record.getLink();
        for (SymbolTableRecord classMember:classTable.records) {
            if (classMember.getKind() == SymbolKind.FUNCTION && !((FunctionType) classMember.getType()).hasDefinition) {
                String errorMessage = "Semantic Error - No definition for declared member function at line " +
                        classMember.getLocation() + ": " + classMember.getName() + "\n";
                errors.put(errorMessage, classMember.getLocation());
            }
        }
    }

    private void functionOverloading(SymbolTableRecord record) {
        if (record.overloaded(record)) {
            String errorMessage = "Semantic Warning - Function overloading at line " +
                    record.getLocation() + ": " + record.getName() + "\n";
            errors.put(errorMessage, record.getLocation());
        }
    }

    private void undeclaredClass(SymbolTableRecord record) {
        VariableType variableType = ((VariableType) record.getType());
        String className = variableType.className;

        if (!className.equals("integer") &&
                !className.equals("float") &&
                !className.equals("string")) {
            SymbolTableRecord classRecord = record.getParent().globalSearch(className);
            if (classRecord == null) {
                String errorMessage = "Semantic Error - Undeclared class at line " +
                        record.getLocation() + ": " + className + "\n";
                errors.put(errorMessage, record.getLocation());
            }
        }
    }

    private void errorHandling(ASTNode node) {
        String errorMessage = "Semantic Error - Multiply declared " + node.record.getKind() + " at line " + node.record.getLocation() +
                ": " + node.record.getName() + "\n";

        // remove error node in AST
        ASTNode leftSibling = node.leftmostSibling;
        while (leftSibling != null) {
            if (leftSibling.rightSibling == node) {
                leftSibling.rightSibling = node.rightSibling;
                break;
            }
            leftSibling = leftSibling.rightSibling;
        }

        errors.put(errorMessage, node.record.getLocation());
    }

}
