import java.util.ArrayList;

public class SymbolTableRecord {
    private String name;
    private SymbolKind kind;
    private SymbolType type;
    private SymbolTable link;
    private SymbolTable parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolKind getKind() {
        return kind;
    }

    public void setKind(SymbolKind kind) {
        this.kind = kind;

        if (kind == SymbolKind.FUNCTION) {
            this.type = new FunctionType();
        }
        else if (kind == SymbolKind.VARIABLE || kind == SymbolKind.PARAMETER) {
            this.type = new VariableType();
        }
        else {
            this.type = new SymbolType();
        }
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public SymbolTable getLink() {
        return link;
    }

    public void setLink(SymbolTable link) {
        this.link = link;
    }

    public SymbolTable getParent() {
        return parent;
    }

    SymbolTableRecord(SymbolTable parent) {
        this.parent = parent;
        setKind(SymbolKind.CLASS);
    }

    @Override
    public String toString() {
        String result = name + ", " + kind + ", " + type + ", ";

        if (link != null) {
            result += "\n\n" + link.toString() + "\n";
        }
        else {
            result += "null";
        }

        result += "\n";
        return result;
    }
}
