public class ASTNode {
    ASTNode parent;
    ASTNode leftmostSibling;
    ASTNode rightSibling;
    ASTNode leftmostChild;
    ASTNodeType type;
    Token token;

    ASTNode() {
        parent = null;
        leftmostSibling = this;
        rightSibling = null;
        leftmostChild = null;
        type = ASTNodeType.NULL;
        token = null;
    }

    ASTNode(Token token, SemanticSymbol symbol) {
        this.token = token;
        this.leftmostSibling = this;

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

    @Override
    public String toString() {
        String result = "";
        ASTNode currentChild = leftmostChild;
        while (currentChild != null) {
            result += type.toString() + " -- " + currentChild.type.toString() + "\n";
            result += currentChild.toString();
            currentChild = currentChild.rightSibling;
        }
        return result;
    }
}
