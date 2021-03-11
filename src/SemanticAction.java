import java.util.ArrayList;
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
                symbol.name.equals("visibility") ||
                symbol.name.equals("not") ||
                symbol.name.equals("id") ||
                symbol.name.equals("breakStat") ||
                symbol.name.equals("continueStat")) {
            stack.push(new ASTNode(token, symbol));
        }
        else if (symbol.name.equals("classList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>();
            listItemType.add(ASTNodeType.CLASS_DECLARATION);
            listAction(stack, token, symbol, listItemType);
        }
        else if (symbol.name.equals("funcDefList")) {
            ArrayList<ASTNodeType> listItemType = new ArrayList<>();
            listItemType.add(ASTNodeType.FUNCTION_DEFINITION);
            listAction(stack, token, symbol, listItemType);
        }
        else if (symbol.name.equals("statList")) {
            ArrayList<ASTNodeType> listItemTypes = new ArrayList<>();
            listItemTypes.add(ASTNodeType.IF_STATEMENT);
            listItemTypes.add(ASTNodeType.ASSIGN_STATEMENT);
            listItemTypes.add(ASTNodeType.BREAK_STATEMENT);
            listItemTypes.add(ASTNodeType.CONTINUE_STATEMENT);
            listItemTypes.add(ASTNodeType.READ_STATEMENT);
            listItemTypes.add(ASTNodeType.RETURN_STATEMENT);
            listItemTypes.add(ASTNodeType.WHILE_STATEMENT);
            listItemTypes.add(ASTNodeType.FUNCTION_ASSIGN_STATEMENT);
            listAction(stack, token, symbol, listItemTypes);
        }
        else if (symbol.name.equals("funcBody")) {
            ASTNode funcBody = new ASTNode(token, symbol);

            ASTNode node = stack.empty()? null:stack.peek();
            while (node != null &&
                    (node.type == ASTNodeType.VARIABLE_DECLARATION_LIST ||
                            node.type == ASTNodeType.STATEMENT_LIST)) {
                funcBody.adoptChild(node);
                stack.pop();
                node = stack.peek();
            }

            stack.push(funcBody);
        }
        else if (symbol.name.equals("program")) {
            ASTNode program = new ASTNode(token, symbol);

            ASTNode node = stack.empty()? null:stack.peek();
            while (node != null &&
                    (node.type == ASTNodeType.CLASS_LIST ||
                    node.type == ASTNodeType.FUNCTION_DEFINITION_LIST ||
                    node.type == ASTNodeType.FUNCTION_BODY)) {
                program.adoptChild(node);
                stack.pop();
                node = stack.empty()? null:stack.peek();
            }

            stack.push(program);
        }
    }

    private static void listAction(Stack<ASTNode> stack,
                                   Token token,
                                   SemanticSymbol symbol,
                                   ArrayList<ASTNodeType> listItemTypes) {
        ASTNode listNode = new ASTNode(token, symbol);

        ASTNode child = stack.empty()? null:stack.peek();
        while (child != null && listItemTypes.contains(child.type)) {
            listNode.adoptChild(child);
            stack.pop();
            child = stack.peek();
        }

        stack.push(listNode);
    }



}
