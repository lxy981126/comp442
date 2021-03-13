import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class SemanticAction {

    public static void performAction(Stack<ASTNode> stack, SemanticSymbol symbol, Token token) {
        if (symbol.name.equals("null")) {
            stack.push(new ASTNode());
        }
        else if (symbol.name.equals("addOp") ||
                symbol.name.equals("multOp") ||
                symbol.name.equals("relOp") ||
                symbol.name.equals("sign") ||
                symbol.name.equals("num") ||
                symbol.name.equals("type") ||
                symbol.name.equals("not") ||
                symbol.name.equals("id") ||
                symbol.name.equals("private") ||
                symbol.name.equals("public") ||
                symbol.name.equals("breakStat") ||
                symbol.name.equals("continueStat")) {
            stack.push(new ASTNode(token, symbol));
        }
        else if (symbol.name.equals("program")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.CLASS_LIST, ASTNodeType.FUNCTION_DEFINITION_LIST,
                            ASTNodeType.FUNCTION_BODY));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("classList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>(Arrays.asList(ASTNodeType.CLASS_DECLARATION));
            listAction(stack, symbol, listItemType);
        }
        else if (symbol.name.equals("classDecl")) {
            ArrayList<ASTNodeType> type = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.INHERIT, ASTNodeType.CLASS_DECLARATION_BODY));
            listAction(stack, symbol, type);
        }
        else if (symbol.name.equals("classDeclBody")) {
            ArrayList<ASTNodeType> type = new ArrayList<>(Arrays.asList
                    (ASTNodeType.VISIBILITY, ASTNodeType.MEMBER_DECLARATION));
            listAction(stack, symbol, type);
        }
        else if (symbol.name.equals("classMethod")) {
            listAction(stack, symbol, new ArrayList<>(Arrays.asList(ASTNodeType.ID)));
        }
        else if (symbol.name.equals("inherit")) {
            listAction(stack, symbol, new ArrayList<>(Arrays.asList(ASTNodeType.ID)));
        }
        else if (symbol.name.equals("funcDefList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>(Arrays.asList(ASTNodeType.FUNCTION_DEFINITION));
            listAction(stack, symbol, listItemType);
        }
        else if (symbol.name.equals("funcDef")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
            (Arrays.asList(ASTNodeType.FUNCTION_HEAD, ASTNodeType.FUNCTION_BODY));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcHead")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.CLASS_METHOD, ASTNodeType.FPARAM_LIST, ASTNodeType.TYPE));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcBody")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.METHOD_BODY, ASTNodeType.STATEMENT_LIST));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("membDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.FUNCTION_DECLARATION, ASTNodeType.VARIABLE_DECLARATION));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.FPARAM_LIST, ASTNodeType.TYPE));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("fParamList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.FPARAM));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("fParam")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.TYPE, ASTNodeType.ID, ASTNodeType.ARRAY_SIZE_LIST));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("methodBody")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.VARIABLE_DECLARATION_LIST));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("varDeclList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.VARIABLE_DECLARATION));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("varDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.TYPE, ASTNodeType.ID, ASTNodeType.ARRAY_SIZE_LIST));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("arraySizeList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.NUM));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("var")) { //TODO not sure
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.ID, ASTNodeType.INDEX_LIST));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("indexList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.EXPRESSION));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("statBlock")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.STATEMENT_LIST,
                    ASTNodeType.IF_STATEMENT, ASTNodeType.ASSIGN_STATEMENT, ASTNodeType.BREAK_STATEMENT,
                    ASTNodeType.CONTINUE_STATEMENT, ASTNodeType.READ_STATEMENT, ASTNodeType.RETURN_STATEMENT,
                    ASTNodeType.WHILE_STATEMENT, ASTNodeType.ASSIGN_STATEMENT));
            listAction(stack, symbol, types);
        }
        else if (symbol.name.equals("statList")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.IF_STATEMENT, ASTNodeType.ASSIGN_STATEMENT, ASTNodeType.BREAK_STATEMENT,
                            ASTNodeType.CONTINUE_STATEMENT, ASTNodeType.READ_STATEMENT, ASTNodeType.RETURN_STATEMENT,
                            ASTNodeType.WHILE_STATEMENT, ASTNodeType.ASSIGN_STATEMENT));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("ifStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION, ASTNodeType.STATEMENT_BLOCK, ASTNodeType.STATEMENT_BLOCK));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("whileStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION, ASTNodeType.STATEMENT_BLOCK));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("readStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.VARIABLE));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("writeStat") || symbol.name.equals("returnStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("assignStat")) { // TODO not sure
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.ID, ASTNodeType.INDEX_LIST, ASTNodeType.APARAM_LIST, ASTNodeType.ADD_OP,
                    ASTNodeType.EXPRESSION));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("factor")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.NUM, ASTNodeType.STRING, ASTNodeType.EXPRESSION, ASTNodeType.NOT,
                    ASTNodeType.SIGN));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("funcOrVar")) {// TODO
        }
        else if (symbol.name.equals("aParamList")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(Arrays.asList(ASTNodeType.APARAM));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("aParam")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(Arrays.asList(ASTNodeType.EXPRESSION));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("expr")) { //TODO not sure
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.ARITHMETIC_EXPRESSION, ASTNodeType.REL_OP));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("arithExpr")) { //TODO not sure
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.TERM, ASTNodeType.ADD_OP));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("term")) { //TODO not sure
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.FACTOR, ASTNodeType.MULT_OP));
            listAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("visibility")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.PRIVATE, ASTNodeType.PUBLIC));
            listAction(stack, symbol, listItemTypes);
        }
    }

    private static void doAction(Stack<ASTNode> stack,
                                   SemanticSymbol symbol,
                                   ArrayList<ASTNodeType> types) {
        ASTNode parent = new ASTNode(symbol);

        int i = types.size() - 1;
        ASTNode child = stack.empty()? null:stack.peek();
        while (child != null && i >= 0 && child.type == types.get(i)) {
            parent.adoptChild(child);
            stack.pop();
            child = stack.empty()? null:stack.peek();
            i--;
        }

        stack.push(parent);
    }

    private static void listAction(Stack<ASTNode> stack,
                                   SemanticSymbol symbol,
                                   ArrayList<ASTNodeType> listItemTypes) {
        ASTNode listNode = new ASTNode(symbol);

        ASTNode child = stack.empty()? null:stack.peek();
        while (child != null && listItemTypes.contains(child.type)) {
            listNode.adoptChild(child);
            stack.pop();
            child = stack.empty()? null:stack.peek();
        }

        stack.push(listNode);
    }

}
