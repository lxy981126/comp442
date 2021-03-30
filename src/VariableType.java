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
    public String toString() {
        String result = className;
        for (Integer i:dimension) {
            result += "[" + i + "]";
        }
        return result;
    }
}
