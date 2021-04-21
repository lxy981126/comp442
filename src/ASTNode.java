import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ASTNode {
    static int idCounter = 0;
    ASTNode parent;
    ASTNode leftmostSibling;
    ASTNode rightSibling;
    ASTNode leftmostChild;
    ASTNodeType type;
    Token token;
    SymbolTable table;
    SymbolTableRecord record;
    int offset;
    int id;

    // leaf nodes
    ASTNode(Token token, SemanticSymbol symbol) {
        id = idCounter;
        idCounter++;
        this.token = token;
        this.leftmostSibling = this;
        assignType(symbol);
    }

    // interior nodes
    ASTNode(SemanticSymbol symbol) {
        id = idCounter;
        idCounter++;
        this.leftmostSibling = this;
        assignType(symbol);
    }

    private void assignType(SemanticSymbol symbol) {
        for (ASTNodeType nodeType:ASTNodeType.values()) {
            boolean equal = symbol.name.equals(nodeType.toString());
            if (equal) {
                this.type = nodeType;
                return;
            }
        }
    }

    public void makeSibling(ASTNode toAdd) {
        ASTNode xSiblings = this;
        while (xSiblings.rightSibling != null) {
            xSiblings = xSiblings.rightSibling;
        }

        ASTNode ySiblings = toAdd.leftmostSibling;
        xSiblings.rightSibling = ySiblings;

        ySiblings.leftmostSibling = xSiblings.leftmostSibling;
        ySiblings.parent = xSiblings.parent;
        while (ySiblings.rightSibling != null) {
            ySiblings = ySiblings.rightSibling;
            ySiblings.leftmostSibling = xSiblings.leftmostSibling;
            ySiblings.parent = xSiblings.parent;
        }
    }

    public void adoptChild(ASTNode toAdd) {
        if (leftmostChild != null){
            leftmostChild.makeSibling(toAdd);
        }
        else {
            ASTNode child = toAdd.leftmostSibling;
            this.leftmostChild = child;
            while (child != null) {
                child.parent = this;
                child = child.rightSibling;
            }
        }
    }

    private String getNodeName() {
        return id + "." + type.toString();
    }

    @Override
    public String toString() {
        String result = "";
        if (leftmostChild == null) {
            result = getNodeName() + "\n";
        }
        else {
            ASTNode currentChild = leftmostChild;
            while (currentChild != null) {
                result +=  getNodeName() + " -- " + currentChild.getNodeName() + "\n";
                result += currentChild.toString();
                currentChild = currentChild.rightSibling;
            }
        }
        return result;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public SymbolTable getTableFromParent() {
        SymbolTable table = this.table;
        ASTNode parent = this.parent;
        while (parent != null &&table == null) {
            table = parent.table;
            parent = parent.parent;
        }
        return table;
    }

    public ASTNode searchParent(ASTNodeType parentType) {
        ASTNode parent = this.parent;
        while (parent != null && parent.type != parentType) {
            parent = parent.parent;
        }
        return parent;
    }

    public void removeChild(ASTNode childToRemove) {
        ASTNode child = leftmostChild;
        if (childToRemove.equals(child)) {
            child.parent.leftmostChild = null;
            return;
        }
        while (child != null && child.rightSibling.equals(childToRemove)) {
            child.rightSibling = childToRemove.rightSibling;
            child = child.rightSibling;
        }
    }

    public ArrayList<ASTNode> getChildrenInOrder() {
        ArrayList<ASTNode> list = new ArrayList<>();
        ArrayList<ASTNode> reversedList = new ArrayList<>();

        ASTNode child = this.leftmostChild;
        while (child != null) {
            list.add(child);
            child = child.rightSibling;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            reversedList.add(list.get(i));
        }
        return reversedList;
    }
}
