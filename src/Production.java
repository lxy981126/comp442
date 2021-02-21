import java.util.ArrayList;

public class Production {
    NonTerminal lhs;
    ArrayList<Symbol> rhs;

    protected Production(NonTerminal lhs) {
        this.lhs = lhs;
        rhs = new ArrayList<>();
    }

    protected void addRHS(Symbol symbol) {
        rhs.add(symbol);
    }

    @Override
    public String toString() {
        String result = lhs.toString() + " -> "; // "<"+lhs+"> -> ";
        for (Symbol symbol:rhs) {
            result += symbol.toString() + " ";
        }
        return result;}
}
