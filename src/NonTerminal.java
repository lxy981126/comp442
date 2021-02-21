import java.util.ArrayList;

public class NonTerminal extends Symbol {
    ArrayList<Symbol> firstSet;
    ArrayList<Symbol> followSet;

    protected NonTerminal(String name) {
        super(name);
        this.firstSet = new ArrayList<>();
        this.followSet = new ArrayList<>();
    }
}
