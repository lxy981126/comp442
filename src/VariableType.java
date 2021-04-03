import java.util.ArrayList;

public class VariableType extends SymbolType {
    String className;
    ArrayList<Integer> dimension;

    VariableType() {
        this.dimension = new ArrayList<>();
    }

    VariableType(String name) {
        super();
        this.className = name;
        this.dimension = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        VariableType variableType = ((VariableType) o);
        if (className.equals(variableType.className) && dimension.equals(variableType.dimension)) {
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
