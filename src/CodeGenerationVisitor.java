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
    protected void visitId(ASTNode node) {
        node.record = node.getTableFromParent().globalSearch(node.token.lexeme);
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

        registerPool.push(localRegister);
    }

    @Override
    protected void visitFactor(ASTNode node) {
        iterateChildren(node);
    }

    @Override
    protected void visitFunctionOrVariable(ASTNode node) {
        iterateChildren(node);
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
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, selectBinaryOperation(operation));
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
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, selectBinaryOperation(operation));
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
            child = child.rightSibling;
        }

        if (lhs != null && rhs != null) {
            String tempVar = createTempVar(node, lhs.record.getSize());
            binaryOperation(lhs, rhs, tempVar, selectBinaryOperation(comparator));
        }
    }

    @Override
    protected void visitAssignStatement(ASTNode node) {
        ASTNode rhsExpression = null;
        ASTNode lhs = null;

        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
            if (child.type == ASTNodeType.EXPRESSION) {
                rhsExpression = child;
            }
            else {
                lhs = child;
            }
            child = child.rightSibling;
        }

        String localRegister = registerPool.pop();
        String lhsOffsetRegister = registerPool.pop();
        String rhsOffsetRegister = registerPool.pop();

        executionCode += indent + "addi " + rhsOffsetRegister + ",r0," + rhsExpression.offset + "\n";
        executionCode += indent + "lw " + localRegister + "," + rhsExpression.record.getName() + "(" + rhsOffsetRegister + ")\n";
        executionCode += indent + "addi " + lhsOffsetRegister + ",r0," + lhs.offset + "\n";
        executionCode += indent + "sw " + lhs.record.getName() + "(" + lhsOffsetRegister + ")," + localRegister + "\n";

        registerPool.push(localRegister);
        registerPool.push(lhsOffsetRegister);
        registerPool.push(rhsOffsetRegister);
    }

    @Override
    protected void visitWriteStatement(ASTNode node) {
        iterateChildren(node);

        String expressionRegister = registerPool.pop();
        String bufferRegister = registerPool.pop();

        executionCode += "% ==== write statement ====\n";
        executionCode += indent + "lw " + expressionRegister + "," + node.record.getName() + "(r0)\n";
        executionCode += indent + "addi r14,r0,topaddr\n"; //todo???
        executionCode += indent + "sw -8(r14)," + expressionRegister + "\n";

        String bufferName = "buffer" + node.id;
        dataCode += bufferName + " res 20\n";
        executionCode += indent + "addi " + bufferRegister + ",r0," + bufferName + "\n";
        executionCode += indent + "sw -12(r14)," + bufferRegister + "\n";

        executionCode += indent + "jl r15, intstr\n";
        executionCode += indent + "addi r14,r0,topaddr\n"; //todo???
        executionCode += indent + "sw -8(r14),r13\n";
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
            child = child.rightSibling;
        }

        expression.accept(this);
        String conditionName = expression.record.getName();
        String elseBlock = "else" + node.id;
        String endIf = "endif" + node.id;

        String conditionRegister = registerPool.pop();
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
            child = child.rightSibling;
        }

        String conditionRegister = registerPool.pop();
        String whileName = "goWhile" + node.id;
        String endWhileName = "endWhile" + node.id;

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
        dataCode += record.getName() + indent + "res " + record.getSize() + "\n";
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
    protected void visitFunctionBody(ASTNode node) {
        iterateChildren(node);
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
        }
    }

    private void binaryOperation(ASTNode lhs, ASTNode rhs, String tempVar, String operation) {
        String lhsRegister = registerPool.pop();
        String lhsOffsetRegister = registerPool.pop();
        String rhsRegister = registerPool.pop();
        String rhsOffsetRegister = registerPool.pop();
        String tempVarRegister = registerPool.pop();

        executionCode += indent + "addi " + rhsOffsetRegister + ",r0," + rhs.offset + "\n";
        executionCode += indent + "lw " + rhsRegister + "," + rhs.record.getName() + "(" + rhsOffsetRegister + ")\n";
        executionCode += indent + "addi " + lhsOffsetRegister + ",r0," + lhs.offset + "\n";
        executionCode += indent + "lw " + lhsRegister + "," + lhs.record.getName() + "(" + lhsOffsetRegister + ")\n";
        executionCode += indent + operation + " " + tempVarRegister + "," + lhsRegister + "," + rhsRegister + "\n";
        executionCode += indent + "sw " + tempVar + "(r0)," + tempVarRegister + "\n";

        registerPool.push(tempVarRegister);
        registerPool.push(lhsRegister);
        registerPool.push(lhsOffsetRegister);
        registerPool.push(rhsRegister);
        registerPool.push(rhsOffsetRegister);
    }

    private String selectBinaryOperation(ASTNode node){
        if (node.type == ASTNodeType.ADD_OP) {
            if (node.token.getType() == TokenType.PLUS) {
                return "add";
            }
            else if (node.token.getType() == TokenType.MINUS) {
                return "sub";
            }
            else {
                return "or";
            }
        }
        else if (node.type == ASTNodeType.MULT_OP){
            if (node.token.getType() == TokenType.MULTIPLICATION) {
                return "mul";
            }
            else if (node.token.getType() == TokenType.DIVISION) {
                return "divv";
            }
            else {
                return "and";
            }
        }
        else if (node.type == ASTNodeType.REL_OP) {
            if (node.token.getType() == TokenType.EQUAL) {
                return "ceq";
            }
            else if (node.token.getType() == TokenType.NOT_EQUAL) {
                return "cne";
            }
            else if (node.token.getType() == TokenType.LESS_THAN) {
                return "clt";
            }
            else if (node.token.getType() == TokenType.GREATER_THAN) {
                return "cgt";
            }
            else if (node.token.getType() == TokenType.LESS_EQUAL) {
                return "cle";
            }
            else { // great or equal
                return "cge";
            }
        }
        else {
            return null;
        }
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
