public class SymbolTableRecord {
    private String name;
    private SymbolKind kind;
    private SymbolType type;
    private SymbolTable link;
    private SymbolTable parent;
    private int location;
    private int size;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

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
        else if (kind == SymbolKind.CLASS) {
            this.type = new ClassType();
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
        link.level = parent.level + 1;
        this.link = link;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    SymbolTableRecord(SymbolTable parent) {
        this.parent = parent;
        setKind(SymbolKind.CLASS);
    }

    SymbolTableRecord(SymbolTableRecord copy) {
        this.name = copy.getName();
        this.link = copy.getLink();
        this.kind = copy.getKind();
        this.type = copy.getType();
        this.parent = copy.getParent();
        this.location = copy.getLocation();
        this.size = copy.getSize();
    }

    public boolean overloaded(SymbolTableRecord record) {
        SymbolTableRecord result;
        SymbolTable parentTable = parent;

        while (parentTable != null) {
            result = parentTable.search(record.getName());
            if (result != null && !result.equals(record)) {
                return true;
            }
            parentTable = parentTable.parent;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        SymbolTableRecord record = ((SymbolTableRecord) o);
        if (record.getKind() == SymbolKind.CLASS) {
            return name.equals(record.getName()) && parent.equals(record.parent);
        }
        return name.equals(record.getName()) && parent.equals(record.parent) && type.equals(record.type);
    }

    public int getElementSize() {
        VariableType variableType = ((VariableType) this.type);
        int size = this.size;

        for (Integer dimension: variableType.dimension) {
            dimension = dimension==null? 16:dimension;
            size /=dimension;
        }

        return size;
    }

    @Override
    public String toString() {
        String result = name + ", " + kind + ", " + type + ", " + size + ", ";

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
