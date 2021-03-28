import java.util.ArrayList;

public class SymbolTableGenerationVisitor extends Visitor{

    @Override
    protected void visitVariableDeclaration(ASTNode node) {
//        SymbolTableRecord record = new SymbolTableRecord()
    }

    @Override
    protected void visitClassList(ASTNode node) { iterateChildren(node); }

    @Override
    protected void visitClassDeclaration(ASTNode node) {
        ASTNode child = node.leftmostChild;
        String className = child.token.lexeme;

        ArrayList<SymbolType> types = new ArrayList<>();
        child = child.rightSibling;
        while (child != null) {
            SymbolType type = new SymbolType(child.token.lexeme, SymbolTypeEnum.CLASS);
            types.add(type);
        }

        SymbolTableRecord record = new SymbolTableRecord(className, SymbolKind.CLASS, types, null, node.table);
        node.table.insert(record);
    }

    @Override
    protected void visitClassDeclarationBody(ASTNode node) {
//        node.record.link = new SymbolTable();

    }

    @Override
    protected void visitProgram(ASTNode node) {
        node.table = new SymbolTable();
        iterateChildren(node);
    }

    private void iterateChildren(ASTNode node) {
        ASTNode child = node.leftmostChild;
        while (child != null) {
            child.accept(this);
        }
    }
}
