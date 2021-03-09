import java.util.Objects;

public class SyntaxSymbol extends Symbol{
    SyntaxSymbolType type;

    public SyntaxSymbol(String name, SyntaxSymbolType type){
        super(name);
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Token) {
            Token token = (Token) o;
            return token.lexeme.equals(name) || token.getType().toString().equals(name);
        }
        else {
            SyntaxSymbol symbol = (SyntaxSymbol) o;
            return type == symbol.type && name.equals(symbol.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
