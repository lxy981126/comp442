public class SymbolType {
    boolean isPrivate;

    SymbolType() {
        isPrivate = true;
    }

    @Override
    public String toString() {
        return isPrivate?"private":"public";
    }
}
