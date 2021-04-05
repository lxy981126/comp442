import java.util.ArrayList;

public class SemanticCheckingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        node.record = node.table.search(node.token.lexeme);
        if (node.record == null) {
            String errorMessage = "Semantic Error - Use of undeclared variable: " + node.token.lexeme +
                    "(line " + node.token.location + ")\n";
            errors.put(errorMessage, node.token.location);
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
            ((VariableType) node.record.getType()).className = "integer";
        }
        else if (node.token.getType() == TokenType.FLOAT_NUMBER) {
            ((VariableType) node.record.getType()).className = "float";
        }
    }

    @Override
    protected void visitFactor(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionOrVariable(ASTNode node) {
        ArrayList<ASTNode> nodes = new ArrayList<>();
        ASTNode parameter = null;
        boolean isFunctionCall = false;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.ID) {
                nodes.add(child);
            }
            if (child.type == ASTNodeType.APARAM_LIST) {
                isFunctionCall = true;
                parameter = child;
            }
            child = child.rightSibling;
        }

        if (isFunctionCall) {
            node.record = functionCall(nodes, parameter);
        }
        else {
            for (ASTNode idNode: nodes) {
                idNode.accept(this);
                node.record = idNode.record;
            }
        }
    }

    private SymbolTableRecord functionCall(ArrayList<ASTNode> idNodes, ASTNode parameterList) {
        SymbolTableRecord returnRecord = new SymbolTableRecord(parameterList.table);
        SymbolTableRecord functionRecord = null;
        SymbolTable table;
        if (idNodes.size() == 1) {
            table = getParentTable(parameterList.table);
        }
        else {
            table = idNodes.get(idNodes.size() - 1).record.getParent();
        }

        for (int i = idNodes.size() - 1; i >= 0; i--) {
            ASTNode idNode = idNodes.get(i);
            SymbolTableRecord childRecord = table.search(idNode.token.lexeme);

            if (childRecord == null) {
                String errorMessage = "Semantic Error - Use of undeclared variable: " + idNode.token.lexeme +
                        "(line " + idNode.record.getLocation() + ")\n";
                errors.put(errorMessage, idNode.record.getLocation());
            }
            else {
                functionRecord = childRecord;
                table = table.parent;
            }
        }

        FunctionType functionType = ((FunctionType) functionRecord.getType());
        returnRecord.setType(functionType.returnType);
        ArrayList<VariableType> functionParameters = functionType.parameters;
        ArrayList<VariableType> givenParameters = new ArrayList<>();

        ASTNode parameter = parameterList.leftmostChild;
        while (parameter != null) {
            parameter.accept(this);
            givenParameters.add(((VariableType) parameter.record.getType()));
            parameter = parameter.rightSibling;
        }

        if (givenParameters.size() != functionParameters.size()) {
            String errorMessage = "Semantic Error - Wrong number of parameter: " + functionRecord.getName() +
                    "(line " + functionRecord.getLocation() + ")\n";
            errors.put(errorMessage, functionRecord.getLocation());
            return returnRecord;
        }

        for (int i = 0; i < functionParameters.size(); i++) {
            VariableType givenParameter = givenParameters.get(i);
            VariableType functionParameter = functionParameters.get(i);
            if (!givenParameter.equals(functionParameter)) {
                String errorMessage = "Semantic Error - Wrong type of parameter: " + givenParameter +
                        "(line " + functionRecord.getLocation() + ")\n";
                errors.put(errorMessage, functionRecord.getLocation());
            }
        }

        return returnRecord;
    }

    @Override
    protected void visitTerm(ASTNode node) {
        ArrayList<SymbolTableRecord> records = new ArrayList<>();

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            if (child.type == ASTNodeType.FACTOR) {
                records.add(child.record);
            }
            child = child.rightSibling;
        }

        checkType(records);
        node.record = records.get(0);
    }

    @Override
    protected void visitArithmeticExpression(ASTNode node) {
        ArrayList<SymbolTableRecord> records = new ArrayList<>();

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            if (child.type == ASTNodeType.TERM) {
                records.add(child.record);
            }
            child = child.rightSibling;
        }

        checkType(records);
        node.record = records.get(0);
    }

    @Override
    protected void visitExpression(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    public void visitAssignStatement(ASTNode node) {
        ArrayList<ASTNode> lhsList = new ArrayList<>();
        SymbolTableRecord lhs = null;
        SymbolTableRecord rhs = null;
        boolean isFunctionCall = false;
        ASTNode parameter = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.ID) {
                lhsList.add(child);
            }
            else if (child.type == ASTNodeType.EXPRESSION) {
                child.accept(this);
                rhs = child.record;
            }
            else if (child.type == ASTNodeType.APARAM_LIST) {
                isFunctionCall = true;
                parameter = child;
            }
            child = child.rightSibling;
        }

        if (isFunctionCall) {
            functionCall(lhsList, parameter);
            return;
        }

        SymbolTable table = lhsList.get(lhsList.size() - 1).record.getParent();
        for (int i = lhsList.size() - 1; i >= 0; i--) {
            ASTNode idNode = lhsList.get(i);
            SymbolTableRecord childRecord = table.search(idNode.token.lexeme);
            if (childRecord == null) {
                String errorMessage = "Semantic Error - Use of undeclared variable: " + idNode.token.lexeme +
                        "(line " + idNode.record.getLocation() + ")\n";
                errors.put(errorMessage, idNode.record.getLocation());
            }
            else {
                lhs = childRecord;
                table = table.parent;
            }
        }

        if (lhs != null && rhs != null) {
            checkType(lhs.getName(), rhs.getName(), lhs.getType(), rhs.getType(), lhs.getLocation(), rhs.getLocation());
        }
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        SymbolTableRecord returnRecord = node.table.parent.search(node.table.name);
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
    protected void visitArrayParameterList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitArrayParameter(ASTNode node) {
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

    private void checkType(ArrayList<SymbolTableRecord> records) {
        for (int i = 1; i < records.size(); i++) {
            SymbolTableRecord record1 = records.get(i - 1);
            SymbolTableRecord record2 = records.get(i);
            checkType(record1.getName(), record2.getName(), record1.getType(), record2.getType(), record1.getLocation(),
                    record2.getLocation());
        }
    }

    private void checkType(String name1, String name2, SymbolType type1, SymbolType type2, int location1, int location2) {
        String errorMessage = "Semantic Error - Mismatch type: " + name1 + "(line " + location1 + "), " +
                name2 + "(line " + location2 + ")\n";

        if (!type1.equals(type2)) {
            errors.put(errorMessage, location1);
        }
    }

    private SymbolTable getParentTable(SymbolTable table) {
        while (table.parent != null) {
            table = table.parent;
        }
        return table;
    }

}