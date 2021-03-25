import java.util.HashMap;

public class SymbolTable {
    private String name;
    private SymbolTable parent;
    private HashMap<String, SymbolTableRecord> records;

    public SymbolTable() {
        this.name = "Global";
        this.parent = null;
        this.records = new HashMap<>();
    }

    private SymbolTable(String name, SymbolTable parent) {
        this.name = name;
        this.parent = parent;
        this.records = new HashMap<>();
    }

    public void insert(SymbolTableRecord record) {
        records.put(record.name, record);
    }

    //todo
    public void delete(String name) {
    }

    public SymbolTableRecord search(String name) {
        return records.get(name);
    }

    @Override
    public String toString() {
        String result = "";

        for (SymbolTableRecord record: records.values()) {
            result += record.toString();
        }

        return result;
    }

    public String getName() { return name; }
}
