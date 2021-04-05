import java.util.ArrayList;

public class VariableType extends SymbolType {
    String className;
    ArrayList<Integer> dimension;

    VariableType() {
        this.dimension = new ArrayList<>();
    }

    VariableType(VariableType type) {
        this.className = new String(type.className);
        this.dimension = new ArrayList<>(type.dimension);
    }

    VariableType(String name) {
        super();
        this.className = name;
        this.dimension = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        VariableType variableType = ((VariableType) o);
        if (className.equals(variableType.className) && dimension.size() == variableType.dimension.size()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = className;
        for (Integer i:dimension) {
            if (i != null)
            {
                result += "[" + i + "]";
            }
            else {
                result += "[]";
            }
        }
        return result;
    }
}
