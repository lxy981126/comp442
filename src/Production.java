import java.util.ArrayList;

public class Production {
    SyntaxSymbol lhs;
    ArrayList<Symbol> rhs;

    protected Production(SyntaxSymbol lhs) {
        this.lhs = lhs;
        rhs = new ArrayList<>();
    }

    protected void addRHS(Symbol symbol) {
        rhs.add(symbol);
    }

    @Override
    public String toString() {
        String result = lhs.toString() + " -> ";
        for (Symbol symbol:rhs) {
            result += symbol.toString() + " ";
        }
        return result;
    }
}
