import java.util.ArrayList;

public class Production {
    String lhs;
    ArrayList<String> rhs;

    protected Production(String lhs) {
        this.lhs = lhs;
        rhs = new ArrayList<>();
    }

    protected void addRHS(String symbol) {
        rhs.add(symbol);
    }

    @Override
    public String toString() {
        String result = lhs.toString() + " -> "; // "<"+lhs+"> -> ";
        for (String symbol:rhs) {
            result += symbol.toString() + " ";
        }
        return result;}
}
