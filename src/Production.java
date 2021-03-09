import java.util.ArrayList;

public class Production {
    SyntaxSymbol lhs;
    ArrayList<SyntaxSymbol> rhs;

    protected Production(SyntaxSymbol lhs) {
        this.lhs = lhs;
        rhs = new ArrayList<>();
    }

    protected void addRHS(SyntaxSymbol symbol) {
        rhs.add(symbol);
    }

    @Override
    public String toString() {
        String result = lhs.toString() + " -> "; // "<"+lhs+"> -> ";
        for (SyntaxSymbol symbol:rhs) {
            result += symbol.toString() + " ";
        }
        return result;
    }
}
