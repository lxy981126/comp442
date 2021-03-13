public class SemanticSymbol extends Symbol{

    public SemanticSymbol(String name) {
        super(name);
    }
    @Override
    public String toString() {
        return "#" + name + "#";
    }
}
