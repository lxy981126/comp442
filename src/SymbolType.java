import java.util.ArrayList;

public class SymbolType {
    String id;
    SymbolTypeEnum type;
    ArrayList<Integer> dimension;

    SymbolType(String id, SymbolTypeEnum type) {
        this.id = id;
        this.type = type;
    }

    SymbolType(SymbolTypeEnum type, ArrayList<Integer> dimension) {
        this.type = type;
        this.dimension = dimension;
    }
}
