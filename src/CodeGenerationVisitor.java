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
        iterateChildren(node);

        ASTNode id = null;
        ASTNode index = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.ID) {
                id = child;
            }
            else if (child.type == ASTNodeType.INDEX_LIST){
                index = child;
            }
            child = child.rightSibling;
        }

        if (index != null) {
            String tempVar = "t" + node.id;
            node.record.setName(tempVar);
            dataCode += tempVar + indent + "res " + id.record.getSize() + "\n";
            int offset = node.offset * node.record.getSize();

            String offsetRegister = registerPool.pop();
            String tempVarRegister = registerPool.pop();

            executionCode += indent + "addi " + offsetRegister + ",r0," + offset + "\n";
            executionCode += indent + "lw " + tempVarRegister + "," + id.record.getName() + "(" + offsetRegister + ")\n";
            executionCode += indent + "sw " + tempVar + "(r0)," + tempVarRegister + "\n";

            registerPool.push(offsetRegister);
            registerPool.push(tempVarRegister);
        }
    }

    @Override
    protected void visitTerm(ASTNode node) {
        ASTNode lhs = null;
        ASTNode rhs = null;
        ASTNode operation = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
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
        ASTNode lhs = null;
        ASTNode index = null;
        ASTNode functionParameters = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            if (child.type == ASTNodeType.APARAM_LIST) {
                functionParameters = child;
                child = child.rightSibling;
                continue;
            }

            child.accept(this);
            if (child.type == ASTNodeType.EXPRESSION) {
                rhsExpression = child;
            }
            else if (child.type == ASTNodeType.INDEX_LIST) {
                index = child;
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

        String localRegister = registerPool.pop();
        String lhsOffsetRegister = registerPool.pop();

        executionCode += "% ==== assign statement ====\n";
        if (functionParameters == null) {
            if (index == null) {
                executionCode += indent + "addi " + lhsOffsetRegister + ",r0,0\n";
            }
            else {
                executionCode += indent + "lw " + lhsOffsetRegister + "," + index.record.getName() + "(r0)\n";
                executionCode += indent + "muli " + lhsOffsetRegister + "," + lhsOffsetRegister + "," + rhsExpression.record.getSize() +"\n";
            }
            executionCode += indent + "lw " + localRegister + "," + rhsExpression.record.getName() + "(r0)\n";
            executionCode += indent + "sw " + lhs.record.getName() + "(" + lhsOffsetRegister + ")," + localRegister + "\n";
        }
        else {
            // todo: function call
            functionParameters.record = node.record;
            functionParameters.accept(this);

            executionCode += indent + "addi r15,r0,topaddr\n"; //todo???
            executionCode += indent + "jl r15," + lhs.record.getName() + "\n";
        }

        registerPool.push(localRegister);
        if (!lhsOffsetRegister.equals("r0")) {
            registerPool.push(lhsOffsetRegister);
        }
    }

    @Override
    protected void visitReadStatement(ASTNode node) {
        iterateChildren(node);

        String bufferRegister = registerPool.pop();
        String bufferName = "buffer" + node.id;

        executionCode += "% ==== write statement ====\n";
        executionCode += "%     ==== pass parameter ====\n";
        dataCode += bufferName + indent + "res 20\n";
        executionCode += indent + "addi " + bufferRegister + ",r0," + bufferName + "\n";
        executionCode += indent + "addi r14,r0,topaddr\n"; //todo???
        executionCode += indent + "sw -8(r14)," + bufferRegister + "\n";

        executionCode += "%     ==== getstr call ====\n";
        executionCode += indent + "jl r15, getstr\n";
        String varName = node.record.getParent().name + "_" + node.record.getName();
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
        executionCode += indent + "jr r15\n";
    }

    @Override
    protected void visitFunctionBody(ASTNode node) {
        executionCode += "% ==== function body:" + node.record.getName() + " =====\n";

        String functionName = node.record.getParent().getName() + "_" + node.record.getName();
        executionCode += functionName + "\n";

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

        ArrayList<ASTNode> list = new ArrayList<>();
        ASTNode child = node.leftmostChild;
        while (child != null) {
            list.add(child);
            child = child.rightSibling;
        }

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
        executionCode += "entry\n";
        iterateChildren(node);
        executionCode += "hlt\n";

        outputToFile();
    }

    private void iterateChildren(ASTNode node) {
        ArrayList<ASTNode> list = new ArrayList<>();

        ASTNode child = node.leftmostChild;
        while (child != null) {
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
