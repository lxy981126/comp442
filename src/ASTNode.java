public class ASTNode {
    static int idCounter = 0;
    ASTNode parent;
    ASTNode leftmostSibling;
    ASTNode rightSibling;
    ASTNode leftmostChild;
    ASTNodeType type;
    Token token;
    int id;

    ASTNode() {
        id = idCounter;
        idCounter++;
        parent = null;
        leftmostSibling = this;
        rightSibling = null;
        leftmostChild = null;
        type = ASTNodeType.NULL;
        token = null;
    }

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
}
