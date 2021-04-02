import java.util.ArrayList;

public class ClassType extends SymbolType{
    ArrayList<String> parents;

    ClassType() { this.parents = new ArrayList<>(); }

    @Override
    public String toString() {
        String result = "";
        for (String parent:parents) {
            result += parent + " ";
        }
        return result;
    }
}
