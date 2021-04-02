import java.util.ArrayList;

public class FunctionType extends SymbolType {
    VariableType returnType;
    String scope;
    ArrayList<VariableType> parameters;

    FunctionType() {
        parameters = new ArrayList<>();
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
