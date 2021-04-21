import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SemanticCheckingVisitor extends Visitor{

    @Override
    protected void visitId(ASTNode node) {
        node.record = node.getTableFromParent().globalSearch(node.token.lexeme);
        if (node.record == null) {
            String errorMessage = "Semantic Error - Use of undeclared variable: " + node.token.lexeme +
                    "(line " + node.token.location + ")\n";
            errors.put(errorMessage, node.token.location);
            node.parent.removeChild(node);
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
    protected void visitIndexList(ASTNode node) {iterateChildren(node);}

    @Override
    protected void visitFactor(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionOrVariable(ASTNode node) {
        ArrayList<ASTNode> ids = new ArrayList<>();
        ArrayList<ASTNode> indexes = new ArrayList<>();
        ArrayList<ASTNode> parametersList = new ArrayList<>();

        boolean indexError = false;
        ArrayList<ASTNode> list = node.getChildrenInOrder();
        for (int i = 0; i < list.size(); i++) {
            ASTNode child = list.get(i);
            if (child.type == ASTNodeType.ID){
                ids.add(child);
                indexes.add(null);
                parametersList.add(null);
            }
            else if (child.type == ASTNodeType.INDEX_LIST) {
                if (ids.get(ids.size() - 1) != null) {
                    indexError = true;
                }
                else {
                    indexes.set(ids.size() - 1, child);
                }
            }
            else if (child.type == ASTNodeType.APARAM_LIST) {
                parametersList.set(ids.size() - 1, child);
            }
        }
        if (checkFunctionOrVariable(node, ids, indexes, parametersList)) {
            return;
        }

        if (indexError) {
            String errorMessage = "Semantic Error - Use of array with wrong number of dimensions: " + node.record.getName() +
                    "(line " + node.record.getLocation() + ")\n";
            errors.put(errorMessage, node.record.getLocation());
            node.parent.removeChild(node);
        }
    }

    @Override
    protected void visitTerm(ASTNode node) {
        ArrayList<SymbolTableRecord> records = new ArrayList<>();

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.accept(this);
            if (child.type == ASTNodeType.FACTOR) {
                records.add(child.record);
            }
            child = child.rightSibling;
        }

        if (checkType(records)) {
            node.parent.removeChild(child.parent);
        }
        node.record = records.get(0);
    }

    @Override
    protected void visitArithmeticExpression(ASTNode node) {
        ArrayList<SymbolTableRecord> records = new ArrayList<>();

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.accept(this);
            if (child.type == ASTNodeType.TERM) {
                records.add(child.record);
            }
            child = child.rightSibling;
        }

        if (checkType(records)) {
            node.parent.removeChild(node);
        }
        node.record = records.get(0);
    }

    @Override
    protected void visitExpression(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    public void visitAssignStatement(ASTNode node) {
        ASTNode rhsExpression = null;
        ArrayList<ASTNode> ids = new ArrayList<>();
        ArrayList<ASTNode> indexes = new ArrayList<>();
        ArrayList<ASTNode> parametersList = new ArrayList<>();

        boolean indexError = false;
        ArrayList<ASTNode> list = node.getChildrenInOrder();
        for (int i = 0; i < list.size(); i++) {
            ASTNode child = list.get(i);
            if (child.type == ASTNodeType.ID){
                ids.add(child);
                indexes.add(null);
                parametersList.add(null);
            }
            else if (child.type == ASTNodeType.INDEX_LIST) {
                if (ids.get(ids.size() - 1) != null) {
                    indexError = true;
                }
                else {
                    indexes.set(ids.size() - 1, child);
                }
            }
            else if (child.type == ASTNodeType.APARAM_LIST) {
                parametersList.set(ids.size() - 1, child);
            }
            else if (child.type == ASTNodeType.EXPRESSION) {
                rhsExpression = child;
                rhsExpression.accept(this);
            }
        }
        if (checkFunctionOrVariable(node, ids, indexes, parametersList))
        {
            return;
        }

        if (indexError) {
            String errorMessage = "Semantic Error - Use of array with wrong number of dimensions: " + node.record.getName() +
                    "(line " + node.record.getLocation() + ")\n";
            errors.put(errorMessage, node.record.getLocation());
            node.parent.removeChild(node);
        }

        SymbolType rhsType;
        if (rhsExpression.record.getType() instanceof VariableType) {
            rhsType = rhsExpression.record.getType();
        }
        else {
            rhsType = ((FunctionType) rhsExpression.record.getType()).returnType;
        }

        if (checkType(node.record.getName(), rhsExpression.record.getName(),node.record.getType(),rhsType,
                node.record.getLocation())) {
            node.parent.removeChild(node);
        }
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        SymbolTableRecord returnRecord = node.table.parent.search(node.table.name);
        SymbolTableRecord givenRecord = null;
        FunctionType returnType = ((FunctionType) returnRecord.getType());

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.table = node.table;
            child.accept(this);
            givenRecord = child.record;
            child = child.rightSibling;
        }
        if (returnRecord != null && givenRecord != null) {
            if (checkType(returnRecord.getName(), givenRecord.getName(), returnType.returnType, givenRecord.getType(),
                    returnRecord.getLocation())) {
                node.parent.removeChild(node);
            }
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
        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.FUNCTION_BODY) {
                child.table = node.table.globalSearch("main").getLink();
            }
            else {
                child.table = node.table;
            }
            child.accept(this);
            node.record = child.record;
            child = child.rightSibling;
        }
    }

    private void iterateChildren(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            node.record = child.record;
            child = child.rightSibling;
        }
    }

    private boolean checkFunctionOrVariable(ASTNode node, ArrayList<ASTNode> ids, ArrayList<ASTNode> indexes,
                                         ArrayList<ASTNode> parametersList) {

        int indexDimension = 0;
        VariableType variableType = null;
        for (int i = 0; i < ids.size(); i++) {
            ASTNode id = ids.get(i);
            id.accept(this);
            if (id.record == null) {
                node.parent.removeChild(node);
                return true;
            }

            variableType = id.record.getType() instanceof VariableType ? ((VariableType) id.record.getType()):null;
            if (id.record != null) {
                node.record = id.record;
            }

            if (i+1 < ids.size()) {
                ASTNode nextId = ids.get(i+1);
                SymbolTableRecord classRecord = id.table.globalSearch(variableType.className);
                nextId.table = classRecord.getLink();
            }

            ASTNode index = indexes.get(i);
            if (index != null) {
                index.accept(this);
                indexDimension++;
                if (index.record == null || !((VariableType) index.record.getType()).className.equals("integer")){
                    String errorMessage = "Semantic Error - Array index is not an integer: " + id.record.getName() +
                            "(line " + id.record.getLocation() + ")\n";
                    errors.put(errorMessage, id.record.getLocation());
                    node.parent.removeChild(node);
                }
            }
            else {
                indexDimension = 0;
            }

            ASTNode parameters = parametersList.get(i);
            if (parameters != null){
                parameters.record = node.record;
                parameters.accept(this);

                ArrayList<SymbolTableRecord> parametersRequired = new ArrayList<>();
                for (SymbolTableRecord record: node.record.getLink().records) {
                    if (record.getKind() == SymbolKind.PARAMETER) {
                        parametersRequired.add(record);
                    }
                }

                int parameterCounter = 0;
                ASTNode parameter = parameters.leftmostChild;
                while (parameter != null) {
                    SymbolTableRecord parameterRequired = parametersRequired.get(parameterCounter);
                    if (!parameter.record.getType().equals(parameterRequired.getType())) {
                        String errorMessage = "Semantic Error - Wrong type of parameter: " + parameterRequired.getName() +
                                "(line " + parameter.record.getLocation() + ")\n";
                        errors.put(errorMessage, parameter.record.getLocation());
                        node.parent.removeChild(node);
                    }
                    parameter = parameter.rightSibling;
                    parameterCounter++;
                }
            }
        }

        if (variableType != null && indexDimension != variableType.dimension.size()) {
            String errorMessage = "Semantic Error - Use of array with wrong number of dimensions: " + node.record.getName() +
                    "(line " + node.record.getLocation() + ")\n";
            errors.put(errorMessage, node.record.getLocation());
            node.parent.removeChild(node);
            return true;
        }
        return false;
    }

    private boolean checkType(ArrayList<SymbolTableRecord> records) {
        boolean mismatchType = false;
        for (int i = 1; i < records.size(); i++) {
            SymbolTableRecord record1 = records.get(i - 1);
            SymbolTableRecord record2 = records.get(i);
            mismatchType = checkType(record1.getName(), record2.getName(), record1.getType(), record2.getType(),
                    record1.getLocation());
        }
        return mismatchType;
    }

    private boolean checkType(String name1, String name2, SymbolType type1, SymbolType type2, int location1) {
        if (!type1.equals(type2)) {
            String errorMessage = "Semantic Error - Mismatch type: " + name1 + ", " +
                    name2 + "(line " + location1 + ")\n";
            errors.put(errorMessage, location1);
            return true;
        }
        return false;
    }

}
