import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class CodeGenerationVisitor extends Visitor{
    private final String indent = "\t\t";
    private Stack<String> registerPool;
    private String executionCode;
    private String dataCode;
    private String outputFile;

    CodeGenerationVisitor(String outputFile) {
        super();
        this.executionCode = "";
        this.dataCode = "";
        this.outputFile = outputFile;

        this.registerPool = new Stack<>();
        for (Integer i = 12; i > 0; i--) {
            registerPool.push("r"+i.toString());
        }
    }

    @Override
    protected void visitAddOperation(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        if (node.token.getType() == TokenType.PLUS) {
            node.record.setName("add");
        }
        else if (node.token.getType() == TokenType.MINUS) {
            node.record.setName("sub");
        }
        else {
            node.record.setName("or");
        }
    }

    @Override
    protected void visitMultOperation(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        if (node.token.getType() == TokenType.MULTIPLICATION) {
            node.record.setName("mul");
        }
        else if (node.token.getType() == TokenType.DIVISION) {
            node.record.setName("divv");
        }
        else {
            node.record.setName("and");
        }
    }

    @Override
    protected void visitRelOperation(ASTNode node) {
        node.record = new SymbolTableRecord(node.table);
        if (node.token.getType() == TokenType.EQUAL) {
            node.record.setName("ceq");
        }
        else if (node.token.getType() == TokenType.NOT_EQUAL) {
            node.record.setName("cne");
        }
        else if (node.token.getType() == TokenType.LESS_THAN) {
            node.record.setName("clt");
        }
        else if (node.token.getType() == TokenType.GREATER_THAN) {
            node.record.setName("cgt");
        }
        else if (node.token.getType() == TokenType.LESS_EQUAL) {
            node.record.setName("cle");
        }
        else { // great or equal
            node.record.setName("cge");
        }
    }

    @Override
    protected void visitId(ASTNode node) {
        node.record = new SymbolTableRecord(node.getTableFromParent().globalSearch(node.token.lexeme));
        node.record.setName(node.record.getParent().name + "_" + node.record.getName());
    }

    @Override
    protected void visitNum(ASTNode node) {
        String variableName = "t" + node.id;

        String localRegister = registerPool.pop();
        executionCode += indent + "addi " + localRegister + ",r0," + node.token.lexeme + "\n";
        executionCode += indent + "sw " + variableName + "(r0)," + localRegister + "\n";

        int size = node.token.getType() == TokenType.INTEGER_NUMBER? 4:8;
        dataCode += variableName + indent + "res " + size + "\n";

        node.record = new SymbolTableRecord(node.table);
        node.record.setName(variableName);
        node.record.setKind(SymbolKind.VARIABLE);
        node.record.setLocation(node.token.location);
        node.record.setSize(size);

        if (node.searchParent(ASTNodeType.INDEX_LIST) != null) {
            node.offset = Integer.parseInt(node.token.lexeme);
        }

        registerPool.push(localRegister);
    }

    @Override
    protected void visitFactor(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionOrVariable(ASTNode node) {
        ArrayList<ASTNode> children = node.getChildrenInOrder();
        ArrayList<ASTNode> ids = new ArrayList<>();
        ArrayList<ASTNode> indexes = new ArrayList<>();
        ArrayList<ASTNode> parametersList = new ArrayList<>();

        for (int i=0;i<children.size();i++) {
            ASTNode child = children.get(i);
            if (child.table == null) {
                child.table = node.table;
            }
            if (child.type == ASTNodeType.ID) {
                ids.add(child);
                indexes.add(null);
                parametersList.add(null);
            }
            else if (child.type == ASTNodeType.INDEX_LIST){
                indexes.set(ids.size()-1, child);
            }
            else if (child.type == ASTNodeType.APARAM_LIST) {
                parametersList.set(ids.size()-1, child);
            }
            if (child.record != null ) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset = child.offset;
            }
        }

        for (int i = 0; i < ids.size(); i++) {
            ASTNode id = ids.get(i);
            id.accept(this);
            if (id.record != null) {
                node.record = id.record;
            }

            if (i+1 < ids.size()) {
                ASTNode nextId = ids.get(i+1);
                VariableType variableType = ((VariableType) id.record.getType());
                SymbolTableRecord classRecord = id.table.globalSearch(variableType.className);
                nextId.table = classRecord.getLink();
            }

            ASTNode index = indexes.get(i);
            ASTNode parameters = parametersList.get(i);
            if (index != null) {
                index.accept(this);
                int tempVarSize = node.record.getElementSize();
                String tempVar = createTempVar(node, tempVarSize);

                String offsetRegister = registerPool.pop();
                String tempVarRegister = registerPool.pop();
                // load index
                executionCode += indent + "lw " + offsetRegister + "," + index.record.getName() + "(r0)\n";
                executionCode += indent + "muli " + offsetRegister + "," + offsetRegister + "," + tempVarSize +"\n";
                // save value to tempVar
                executionCode += indent + "lw " + tempVarRegister + "," + id.record.getName() + "(" + offsetRegister + ")\n";
                executionCode += indent + "sw " + tempVar + "(r0)," + tempVarRegister + "\n";

                registerPool.push(offsetRegister);
                registerPool.push(tempVarRegister);
            }
            if (parameters != null) {
                parameters.record = node.record;
                parameters.accept(this);
                executionCode += indent + "jl r15," + id.record.getName() + "\n";

                String returnValue = parameters.record.getName() + "_return";
                SymbolTableRecord returnRecord = parameters.record.getLink().globalSearch(returnValue);
                node.record = returnRecord;
            }
        }
    }

    @Override
    protected void visitTerm(ASTNode node) {
        ASTNode lhs = null;
        ASTNode rhs = null;
        ASTNode operation = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.table == null) {
                child.table = node.table;
            }
            child.accept(this);
            if (child.type == ASTNodeType.FACTOR) {
                if (rhs == null) {
                    rhs = child;
                }
                else {
                    lhs = child;
                }
            }
            else {
                operation = child;
            }
            if (child.record != null) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset = child.offset;
            }
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, operation.record.getName());
        }
    }

    @Override
    protected void visitArithmeticExpression(ASTNode node) {
        ASTNode lhs = null;
        ASTNode rhs = null;
        ASTNode operation = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.table == null) {
                child.table = node.table;
            }
            child.accept(this);
            if (child.type == ASTNodeType.TERM) {
                if (rhs == null) {
                    rhs = child;
                }
                else {
                    lhs = child;
                }
            }
            else {
                operation = child;
            }
            if (child.record != null) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset += child.offset;
            }
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, operation.record.getName());
        }
    }

    @Override
    protected void visitExpression(ASTNode node) {
        ASTNode lhs = null;
        ASTNode rhs = null;
        ASTNode comparator = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.table == null) {
                child.table = node.table;
            }
            child.accept(this);
            if (child.type == ASTNodeType.REL_OP) {
                comparator = child;
            }
            else if (child.type == ASTNodeType.ARITHMETIC_EXPRESSION && rhs == null) {
                rhs = child;
            }
            else {
                lhs = child;
            }
            if (child.record != null) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset = child.offset;
            }
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, comparator.record.getName());
            node.record.setName(tempVar);
        }
    }

    @Override
    protected void visitIndexList(ASTNode node) {
        executionCode += "%==== index list ====\n";
        iterateChildren(node);
    }

    @Override
    protected void visitVariable(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitAssignStatement(ASTNode node) {
        ASTNode rhsExpression = null;
        ArrayList<ASTNode> ids = new ArrayList<>();
        ArrayList<ASTNode> indexes = new ArrayList<>();
        ArrayList<ASTNode> functionParametersList = new ArrayList<>();

        ArrayList<ASTNode> list = node.getChildrenInOrder();
        for (int i = 0; i < list.size(); i++) {
            ASTNode child = list.get(i);
            if (child.type == ASTNodeType.ID){
                ids.add(child);
                indexes.add(null);
                functionParametersList.add(null);
            }
            else if (child.type == ASTNodeType.INDEX_LIST) {
                indexes.set(ids.size() - 1, child);
            }
            else if (child.type == ASTNodeType.APARAM_LIST) {
                functionParametersList.set(ids.size() - 1, child);
            }
            else if (child.type == ASTNodeType.EXPRESSION) {
                rhsExpression = child;
                rhsExpression.accept(this);
            }
        }

        String localRegister = registerPool.pop();
        String lhsOffsetRegister = registerPool.pop();

        executionCode += "% ==== assign statement ====\n";
        for (int i = 0; i < ids.size(); i++) {
            ASTNode id = ids.get(i);
            id.accept(this);
            if (id.record != null) {
                node.record = id.record;
            }

            if (i+1 < ids.size()) {
                ASTNode nextId = ids.get(i+1);
                VariableType variableType = ((VariableType) id.record.getType());
                SymbolTableRecord classRecord = id.table.globalSearch(variableType.className);
                nextId.table = classRecord.getLink();
            }

            ASTNode index = indexes.get(i);
            ASTNode functionParameters = functionParametersList.get(i);
            if (functionParameters == null) {
                if (index == null) {
                    executionCode += indent + "addi " + lhsOffsetRegister + ",r0,0\n";
                }
                else {
                    index.accept(this);
                    executionCode += indent + "lw " + lhsOffsetRegister + "," + index.record.getName() + "(r0)\n";
                    executionCode += indent + "muli " + lhsOffsetRegister + "," + lhsOffsetRegister + "," + rhsExpression.record.getSize() +"\n";
                }
                executionCode += indent + "lw " + localRegister + "," + rhsExpression.record.getName() + "(r0)\n";
                executionCode += indent + "sw " + id.record.getName() + "(" + lhsOffsetRegister + ")," + localRegister + "\n";
            }
            else {
                functionParameters.record = node.record;
                functionParameters.accept(this);
                executionCode += indent + "jl r15," + id.record.getName() + "\n";
            }
        }
        registerPool.push(localRegister);
        if (!lhsOffsetRegister.equals("r0")) {
            registerPool.push(lhsOffsetRegister);
        }
    }

    @Override
    protected void visitReadStatement(ASTNode node) {
        executionCode += "% ==== read statement ====\n";
        iterateChildren(node);

        String bufferRegister = registerPool.pop();
        String bufferName = "buffer" + node.id;

        executionCode += "%     ==== pass parameter ====\n";
        dataCode += bufferName + indent + "res 20\n";
        executionCode += indent + "addi " + bufferRegister + ",r0," + bufferName + "\n";
        executionCode += indent + "addi r14,r0,topaddr\n";
        executionCode += indent + "sw -8(r14)," + bufferRegister + "\n";

        executionCode += "%     ==== getstr call ====\n";
        executionCode += indent + "jl r15, getstr\n";
        String varName = node.record.getName();
        executionCode += indent + "sw " + varName + "(r0),r13\n";

        registerPool.push(bufferRegister);
    }

    @Override
    protected void visitWriteStatement(ASTNode node) {
        iterateChildren(node);

        String expressionRegister = registerPool.pop();
        String bufferRegister = registerPool.pop();

        executionCode += "% ==== write statement ====\n";
        executionCode += "%     ==== pass parameter ====\n";
        executionCode += indent + "lw " + expressionRegister + "," + node.record.getName() + "(r0)\n";
        executionCode += indent + "addi r14,r0,topaddr\n"; //todo???
        executionCode += indent + "sw -8(r14)," + expressionRegister + "\n";

        executionCode += "%     ==== pass parameter ====\n";
        String bufferName = "buffer" + node.id;
        dataCode += bufferName + indent + "res 20\n";
        executionCode += indent + "addi " + bufferRegister + ",r0," + bufferName + "\n";
        executionCode += indent + "sw -12(r14)," + bufferRegister + "\n";

        executionCode += "%     ==== intstr call ====\n";
        executionCode += indent + "jl r15, intstr\n";

        executionCode += "%     ==== pass parameter ====\n";
        executionCode += indent + "sw -8(r14),r13\n";

        executionCode += "%     ==== putstr call ====\n";
        executionCode += indent + "jl r15,putstr\n";

        registerPool.push(expressionRegister);
        registerPool.push(bufferRegister);
    }

    @Override
    protected void visitIfStatement(ASTNode node) {
        ASTNode expression = null;
        ASTNode statement1 = null;
        ASTNode statement2 = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.EXPRESSION) {
                expression = child;
            }
            else if (statement2 == null) {
                statement2 = child;
            }
            else {
                statement1 = child;
            }
            if (child.record != null) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset = child.offset;
            }
            child = child.rightSibling;
        }

        expression.accept(this);
        String conditionName = expression.record.getName();
        String elseBlock = "else" + node.id;
        String endIf = "endif" + node.id;

        String conditionRegister = registerPool.pop();
        executionCode += "% ==== if statement ====\n";
        executionCode += indent + "lw " + conditionRegister + "," + conditionName + "(r0)\n";
        executionCode += indent + "bz " + conditionRegister + "," + elseBlock + "\n";

        statement1.accept(this);
        executionCode += indent + "j " + endIf + "\n";
        executionCode += elseBlock + "\n";
        statement2.accept(this);
        executionCode += endIf + "\n";

        registerPool.push(conditionRegister);
    }

    @Override
    protected void visitWhileStatement(ASTNode node) {
        ASTNode expression = null;
        ASTNode statementBlock = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.table == null) {
                child.table = node.table;
            }
            if (child.type == ASTNodeType.EXPRESSION) {
                expression = child;
            }
            else if (child.type == ASTNodeType.STATEMENT_BLOCK) {
                statementBlock = child;
            }
            if (child.record != null) {
                node.record = child.record;
            }
            if (child.offset != 0) {
                node.offset = child.offset;
            }
            child = child.rightSibling;
        }

        String conditionRegister = registerPool.pop();
        String whileName = "goWhile" + node.id;
        String endWhileName = "endWhile" + node.id;

        executionCode += "% ==== while loop ====\n";
        executionCode += whileName + "\n";
        expression.accept(this);
        executionCode += indent + "lw " + conditionRegister + "," + expression.record.getName() + "(r0)\n";
        executionCode += indent + "bz " + conditionRegister + "," + endWhileName + "\n";
        statementBlock.accept(this);
        executionCode += indent + "j " + whileName + "\n";
        executionCode += endWhileName + "\n";

        registerPool.push(conditionRegister);
    }

    @Override
    protected void visitReturnStatement(ASTNode node) {
        ASTNode functionBody = node.searchParent(ASTNodeType.FUNCTION_BODY);
        String functionName = functionBody.table.name;
        SymbolTableRecord functionRecord = functionBody.table.globalSearch(functionName);
        functionName = functionBody.table.parent.name + "_" + functionName + "_return";

        String returnVar = functionName;
        dataCode += returnVar + indent + "res " + functionRecord.getSize() + "\n";
        executionCode += "%==== return statement ====\n";
        iterateChildren(node);

        String valueRegister = registerPool.pop();
        String returnValue = node.record.getName();
        executionCode += indent + "lw " + valueRegister + "," + returnValue + "(r0)\n";
        executionCode += indent + "sw " + returnVar + "(r0)," + valueRegister + "\n";

        String returnAddress = "t" + functionBody.id;
        executionCode += indent + "lw r15," + returnAddress + "(r0)\n";
        executionCode += indent + "jr r15\n";

        registerPool.push(valueRegister);
    }

    @Override
    protected void visitStatementBlock(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitStatementList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitVariableDeclaration(ASTNode node) {
        SymbolTableRecord record = node.record;
        dataCode += record.getParent().name + "_" + record.getName() + indent + "res " + record.getSize() + "\n";
    }

    @Override
    protected void visitVariableDeclarationList(ASTNode node) {
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
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionBody(ASTNode node) {
        String recordName = node.table.getName();
        String functionName = node.table.parent.getName() + "_" + recordName;

        executionCode += recordName.equals("main")? "entry\n":"";
        executionCode += "% ==== function body:" + functionName + " =====\n";
        executionCode += functionName + "\n";

        String returnAddress = "t" + node.id;
        dataCode += returnAddress + indent + "res 4\n";
        executionCode += indent + "sw " + returnAddress + "(r0),r15\n";

        iterateChildren(node);

        executionCode += indent + "lw r15," + returnAddress + "(r0)\n";
        executionCode += recordName.equals("main")? "":indent + "jr r15\n";
    }

    @Override
    protected void visitMemberDeclaration(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitClassDeclaration(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitClassList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionHead(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitArrayParameterList(ASTNode node) {
        ArrayList<SymbolTableRecord> parametersRequired = new ArrayList<>();
        for (SymbolTableRecord functionRecord: node.record.getLink().records) {
            if (functionRecord.getKind() == SymbolKind.PARAMETER) {
                parametersRequired.add(functionRecord);
            }
        }

        ArrayList<ASTNode> list = node.getChildrenInOrder();
        for (int i = list.size() - 1; i >= 0; i--) {
            ASTNode parameterGiven = list.get(i);
            parameterGiven.accept(this);

            SymbolTableRecord parameterRequired = parametersRequired.get(i);
            String parameterName = parameterRequired.getParent().getName() + "_" + parameterRequired.getName();

            String parameterRegister = registerPool.pop();
            executionCode += indent + "lw " + parameterRegister + "," + parameterGiven.record.getName() + "(r0)\n";
            executionCode += indent + "sw " + parameterName + "(r0)," + parameterRegister + "\n";
        }
    }

    @Override
    protected void visitArrayParameter(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionParameterList(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionParameter(ASTNode node) {
        iterateChildren(node);
        node.record = new SymbolTableRecord(node.record);
        node.record.setName(node.getTableFromParent().name + "_" + node.record.getName());
        dataCode += node.record.getName() + indent + "res " + node.record.getSize() + "\n";
    }

    @Override
    protected void visitProgram(ASTNode node) {
        ArrayList<ASTNode> list = node.getChildrenInOrder();

        for (int i = 0; i < list.size(); i++) {
            ASTNode currentChild = list.get(i);
            if (list.get(i).type == ASTNodeType.FUNCTION_BODY) {
                currentChild.record = currentChild.table.globalSearch("main");
            }

            currentChild.accept(this);
            if (list.get(i).record != null ) {
                node.record = currentChild.record;
            }
            if (list.get(i).offset != 0) {
                node.offset = currentChild.offset;
            }
        }
        executionCode += "hlt\n";

        outputToFile();
    }

    private void iterateChildren(ASTNode node) {
        ArrayList<ASTNode> list = new ArrayList<>();
        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.table == null) {
                child.table = node.table;
            }
            list.add(child);
            child = child.rightSibling;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            list.get(i).accept(this);
            if (list.get(i).record != null ) {
                node.record = list.get(i).record;
            }
            if (list.get(i).offset != 0) {
                node.offset = list.get(i).offset;
            }
        }
    }

    private void binaryOperation(ASTNode lhs, ASTNode rhs, String tempVar, String operation) {
        String lhsRegister = registerPool.pop();
        String lhsOffsetRegister = registerPool.pop();
        String rhsRegister = registerPool.pop();
        String rhsOffsetRegister = registerPool.pop();
        String tempVarRegister = registerPool.pop();

        executionCode += indent + "lw " + rhsRegister + "," + rhs.record.getName() + "(r0)\n";
        executionCode += indent + "lw " + lhsRegister + "," + lhs.record.getName() + "(r0)\n";
        executionCode += indent + operation + " " + tempVarRegister + "," + lhsRegister + "," + rhsRegister + "\n";
        executionCode += indent + "sw " + tempVar + "(r0)," + tempVarRegister + "\n";

        registerPool.push(tempVarRegister);
        registerPool.push(lhsRegister);
        registerPool.push(lhsOffsetRegister);
        registerPool.push(rhsRegister);
        registerPool.push(rhsOffsetRegister);
    }

    private String createTempVar(ASTNode node, int size) {
        String tempVar = "t" + node.id;
        node.record = new SymbolTableRecord(node.table);
        node.record.setName(tempVar);
        node.record.setKind(SymbolKind.VARIABLE);
        node.record.setSize(size);
        dataCode += tempVar + indent + "res " + size + "\n";
        return tempVar;
    }

    private void outputToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(executionCode);
            writer.flush();
            writer.write(dataCode);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
