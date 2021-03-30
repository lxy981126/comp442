import java.util.HashMap;

public class SymbolTable {
    String name;
    SymbolTable parent;
    HashMap<String, SymbolTableRecord> records;

    SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.records = new HashMap<>();
    }

    SymbolTable(String name, SymbolTable parent) {
        this.name = name;
        this.parent = parent;
        this.records = new HashMap<>();
    }

    public void insert(SymbolTableRecord record) {
        if (records.get(record.getName()) != null) {
            System.err.println("record = " + record);
        }
        records.put(record.getName(), record);
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
