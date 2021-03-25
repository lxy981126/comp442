public class SymbolTableRecord {
    String name;
    SymbolTableRecordType kind;
    String type; //todo change type (String)
    SymbolTable link;
    SymbolTable parent;

    @Override
    public String toString() {
        String result = name + ", " + kind + ", " + type;

        if (link != null) {
            result += ", " + name;
        }

        result += "\n";
        return result;
    }
}
