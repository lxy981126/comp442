import java.util.ArrayList;

public class FunctionType extends SymbolType {
    VariableType returnType;
    ArrayList<VariableType> parameters;

    FunctionType() {
        parameters = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "@return " + returnType.toString() + " (";
        for (VariableType parameter:parameters) {
            result += ", " + parameter.toString();
        }
        result += ")";
        return result;
    }
}
