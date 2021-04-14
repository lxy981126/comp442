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
        node.record = node.table.globalSearch(node.token.lexeme);
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
        iterateChildren(node);
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
