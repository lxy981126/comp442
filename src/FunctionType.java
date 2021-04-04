import java.util.ArrayList;

public class FunctionType extends SymbolType {
    VariableType returnType;
    String scope;
    ArrayList<VariableType> parameters;
    boolean hasDefinition = false;

    FunctionType() {
        parameters = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        FunctionType functionType = ((FunctionType) o);
        if (returnType.equals(functionType.returnType) &&
                scope.equals(functionType.scope) &&
                parameters.equals(functionType.parameters)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = isPrivate? "private ": "public ";
        result += returnType == null? "void: ":returnType.toString() + ": ";

        for (VariableType parameter:parameters) {
            result += parameter.toString() + " ";
        }
        return result;
    }
}
