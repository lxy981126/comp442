public class Token {
    private TokenType type;
    private String lexeme;
    private int location;

    public Token(TokenType type, String lexeme, int location){
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
    }

    @Override
    public String toString(){
        return "["+type+","+lexeme+","+location+"]";
    }

}
