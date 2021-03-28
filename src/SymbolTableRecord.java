import java.util.ArrayList;

public class SymbolTableRecord {
    String name;
    SymbolKind kind;
    ArrayList<SymbolType> type;
    SymbolTable link;
    SymbolTable parent;

    SymbolTableRecord(String name, SymbolKind kind, ArrayList<SymbolType> type, SymbolTable link, SymbolTable parent) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.link = link;
        this.parent = parent;
    }

    @Override
    public String toString() {
        String result = name + ", " + kind + ", " + type;

        if (link != null) {
            result += "\n\n" + link.toString() + "\n\n";
        }

        result += "\n";
        return result;
    }
}
