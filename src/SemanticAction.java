import java.net.IDN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class SemanticAction {

    public static void performAction(Stack<ASTNode> stack, SemanticSymbol symbol, Token token) {
        if (symbol.name.equals("addOp") ||
                symbol.name.equals("multOp") ||
                symbol.name.equals("relOp") ||
                symbol.name.equals("assignOp") ||
                symbol.name.equals("sign") ||
                symbol.name.equals("num") ||
                symbol.name.equals("str") ||
                symbol.name.equals("type") ||
                symbol.name.equals("not") ||
                symbol.name.equals("id") ||
                symbol.name.equals("dot") ||
                symbol.name.equals("visibility") ||
                symbol.name.equals("breakStat") ||
                symbol.name.equals("continueStat")) {
            stack.push(new ASTNode(token, symbol));
        }
        else if (symbol.name.equals("program")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.CLASS_LIST, ASTNodeType.FUNCTION_DEFINITION_LIST,
                            ASTNodeType.FUNCTION_BODY));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("classList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>(Arrays.asList(ASTNodeType.CLASS_DECLARATION));
            doAction(stack, symbol, listItemType);
        }
        else if (symbol.name.equals("classDecl")) {
            ArrayList<ASTNodeType> type = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.INHERIT, ASTNodeType.CLASS_DECLARATION_BODY));
            doAction(stack, symbol, type);
        }
        else if (symbol.name.equals("classDeclBody")) {
            ArrayList<ASTNodeType> type = new ArrayList<>(Arrays.asList
                    (ASTNodeType.VISIBILITY, ASTNodeType.MEMBER_DECLARATION));
            doAction(stack, symbol, type);
        }
        else if (symbol.name.equals("classMethod")) {
            actionInOrder(stack, symbol, new ArrayList<>(Arrays.asList(ASTNodeType.ID)));
        }
        else if (symbol.name.equals("inherit")) {
            inheritAction(stack, symbol);
        }
        else if (symbol.name.equals("funcDefList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>(Arrays.asList(ASTNodeType.FUNCTION_DEFINITION));
            doAction(stack, symbol, listItemType);
        }
        else if (symbol.name.equals("funcDef")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
            (Arrays.asList(ASTNodeType.FUNCTION_HEAD, ASTNodeType.FUNCTION_BODY));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcHead")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.CLASS_METHOD, ASTNodeType.FPARAM_LIST, ASTNodeType.TYPE));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcBody")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.METHOD_BODY, ASTNodeType.STATEMENT_LIST));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("membDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.FUNCTION_DECLARATION, ASTNodeType.VARIABLE_DECLARATION));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("funcDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.ID, ASTNodeType.FPARAM_LIST, ASTNodeType.TYPE));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("fParamList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.FPARAM));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("fParam")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.TYPE, ASTNodeType.ID, ASTNodeType.ARRAY_SIZE_LIST));
            actionInOrder(stack, symbol, types);
        }
        else if (symbol.name.equals("methodBody")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.VARIABLE_DECLARATION_LIST));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("varDeclList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.VARIABLE_DECLARATION));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("varDecl")) {
            ArrayList<ASTNodeType> types = new ArrayList<>
                    (Arrays.asList(ASTNodeType.TYPE, ASTNodeType.ID, ASTNodeType.ARRAY_SIZE_LIST));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("arraySizeList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.NUM));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("var")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.ID, ASTNodeType.INDEX_LIST));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("indexList")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.EXPRESSION));
            actionInOrder(stack, symbol, types);
        }
        else if (symbol.name.equals("statBlock")) {
            ArrayList<ASTNodeType> types = new ArrayList<>(Arrays.asList(ASTNodeType.STATEMENT_LIST,
                    ASTNodeType.IF_STATEMENT, ASTNodeType.ASSIGN_STATEMENT, ASTNodeType.BREAK_STATEMENT,
                    ASTNodeType.CONTINUE_STATEMENT, ASTNodeType.READ_STATEMENT, ASTNodeType.RETURN_STATEMENT,
                    ASTNodeType.WHILE_STATEMENT, ASTNodeType.ASSIGN_STATEMENT));
            doAction(stack, symbol, types);
        }
        else if (symbol.name.equals("statList")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.IF_STATEMENT, ASTNodeType.ASSIGN_STATEMENT, ASTNodeType.BREAK_STATEMENT,
                            ASTNodeType.CONTINUE_STATEMENT, ASTNodeType.READ_STATEMENT, ASTNodeType.RETURN_STATEMENT,
                            ASTNodeType.WHILE_STATEMENT, ASTNodeType.WRITE_STATEMENT));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("ifStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION, ASTNodeType.STATEMENT_BLOCK, ASTNodeType.STATEMENT_BLOCK));
            actionInOrder(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("whileStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION, ASTNodeType.STATEMENT_BLOCK));
            actionInOrder(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("readStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.VARIABLE));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("writeStat") || symbol.name.equals("returnStat")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.EXPRESSION));
            actionInOrder(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("assignStat")) {
            assignStatementAction(stack, symbol);
        }
        else if (symbol.name.equals("factor")) {
            factorAction(stack, symbol);
        }
        else if (symbol.name.equals("funcOrVar")) {
            funcOrVarAction(stack, symbol);
        }
        else if (symbol.name.equals("aParamList")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(Arrays.asList(ASTNodeType.APARAM));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("aParam")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(Arrays.asList(ASTNodeType.EXPRESSION));
            actionInOrder(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("expr")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.ARITHMETIC_EXPRESSION, ASTNodeType.REL_OP));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("arithExpr")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.TERM, ASTNodeType.ADD_OP));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("term")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(
                    Arrays.asList(ASTNodeType.FACTOR, ASTNodeType.MULT_OP));
            doAction(stack, symbol, listItemTypes);
        }
        else if (symbol.name.equals("visibility")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>(Arrays.asList(ASTNodeType.VISIBILITY));
            doAction(stack, symbol, listItemTypes);
        }
    }
    
    private static void doAction(Stack<ASTNode> stack,
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

    private static void actionInOrder(Stack<ASTNode> stack,
                                      SemanticSymbol symbol,
                                      ArrayList<ASTNodeType> listItemTypes) {
        ASTNode listNode = new ASTNode(symbol);
        int i = listItemTypes.size() - 1;

        ASTNode child = stack.empty()? null:stack.peek();
        while (i>=0 && child.type == listItemTypes.get(i)) {
            listNode.adoptChild(child);
            stack.pop();
            child = stack.empty()? null:stack.peek();
            i--;
        }

        stack.push(listNode);
    }

    private static void inheritAction(Stack<ASTNode> stack, SemanticSymbol symbol) {
        ASTNode inheritNode = new ASTNode(symbol);

        ASTNode child = stack.empty()? null:stack.peek();
        while (child != null && child.type == ASTNodeType.ID) {
            stack.pop();

            ASTNode nextNode = stack.empty()? null:stack.peek();
            if (nextNode == null || nextNode.type != ASTNodeType.ID) {
                stack.push(child);
                break;
            }

            inheritNode.adoptChild(child);
            child = nextNode;
        }
        stack.push(inheritNode);
    }

    private static void funcOrVarAction(Stack<ASTNode> stack, SemanticSymbol symbol) {
        ASTNode funcOrVar = new ASTNode(symbol);
        adoptFuncVar(stack, funcOrVar);
        stack.push(funcOrVar);
    }

    private static void adoptFuncVar(Stack<ASTNode> stack, ASTNode parent) {
        ArrayList<ASTNodeType> types = new ArrayList<>(List.of(ASTNodeType.TYPE, ASTNodeType.APARAM_LIST,
                ASTNodeType.INDEX_LIST, ASTNodeType.DOT, ASTNodeType.ID));
        boolean canAdoptId = true;

        ASTNode child = stack.empty()? null:stack.peek();
        while (child != null && types.contains(child.type)) {
            if (child.type == ASTNodeType.DOT) {
                parent.adoptChild(child);
                stack.pop();
                canAdoptId = true;
            }
            else if (child.type == ASTNodeType.ID){
                if (canAdoptId) {
                    parent.adoptChild(child);
                    stack.pop();
                    canAdoptId = false;
                }
                else {
                    return;
                }
            }
            else {
                parent.adoptChild(child);
                stack.pop();
            }
            child = stack.empty()? null:stack.peek();
        }
    }

    private static void factorAction(Stack<ASTNode> stack,
                              SemanticSymbol symbol) {
        ASTNode factorNode = new ASTNode(symbol);

        ASTNode child = stack.empty()? null:stack.peek();
        if (child.type == ASTNodeType.NUM ||
                child.type == ASTNodeType.STRING ||
                child.type == ASTNodeType.FUNCTION_OR_VARIABLE) {
            factorNode.adoptChild(child);
            stack.pop();
        }
        else if (child.type == ASTNodeType.FACTOR){
            stack.pop();
            if (!stack.empty() && (stack.peek().type == ASTNodeType.SIGN || stack.peek().type == ASTNodeType.NOT)) {
                factorNode.adoptChild(child);
                factorNode.adoptChild(stack.peek());
                stack.pop();
            }
            else {
                stack.push(child);
            }
        }
        else if (child.type == ASTNodeType.EXPRESSION) {
            stack.pop();
            ASTNode expression1 = null;
            if (!stack.empty()) {
                expression1 = stack.peek();
                stack.pop();
            }

            ASTNode expression2 = null;
            if (!stack.empty()) {
                expression2 = stack.peek();
                stack.pop();
            }

            if (expression1 != null && expression2 != null &&
                    expression1.type == ASTNodeType.EXPRESSION && expression2.type == ASTNodeType.EXPRESSION) {
                factorNode.adoptChild(expression1);
                factorNode.adoptChild(expression2);
                factorNode.adoptChild(child);
            }
            else {
                if (expression2 != null) {
                    stack.push(expression2);
                }
                if (expression1 != null) {
                    stack.push(expression1);
                }
                factorNode.adoptChild(child);
            }
        }
        stack.push(factorNode);
    }

    private static void assignStatementAction(Stack<ASTNode> stack, SemanticSymbol symbol) {
        ASTNode assignNode = new ASTNode(symbol);

        ASTNode child = stack.empty()? null:stack.peek();
        if (child != null && child.type == ASTNodeType.EXPRESSION) {
            assignNode.adoptChild(child);
            stack.pop();
            child = stack.empty()? null:stack.peek();
        }
        if (child != null && child.type == ASTNodeType.ASSIGN_OP) {
            assignNode.adoptChild(child);
            stack.pop();
            child = stack.empty()? null:stack.peek();
        }
        adoptFuncVar(stack, assignNode);
        stack.push(assignNode);
    }
}
