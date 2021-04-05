import java.util.ArrayList;

public class SymbolTable {
    String name;
    SymbolTable parent;
    ArrayList<SymbolTableRecord> records;
    int level;

    SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.records = new ArrayList<>();
        this.level = parent.level + 1;
    }

    SymbolTable(String name, SymbolTable parent) {
        this.name = name;
        this.parent = parent;
        this.records = new ArrayList<>();
        this.level = 0;
    }

    public boolean insert(SymbolTableRecord record) {
        if (record.getName() == null) {
            System.out.println(record);
        }
        if (records.contains(record)) {
            return true;
        }
        records.add(record);
        return false;
    }

    public void delete(SymbolTableRecord name) { records.remove(name); }

    public SymbolTableRecord search(String name) {
        for (SymbolTableRecord record: records) {
            if (record.getName().equals(name)) {
                return record;
            }
        }
        return null;
    }

    public SymbolTableRecord globalSearch(String name) {
        SymbolTableRecord result = search(name);
        if (result != null) {
            return result;
        }

        SymbolTable parentTable = parent;
        while (parentTable != null) {
            result = parentTable.search(name);
            if (result != null) {
                break;
            }
            parentTable = parentTable.parent;
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "";

        for (SymbolTableRecord record: records) {
            for (int i = 0; i < level; i++) {
                result += "\t";
            }
            result += record.toString();
        }

        return result;
    }

    public String getName() { return name; }
}
