public class Token {
    private TokenType type;
    protected String lexeme;
    protected int location;

    public Token(TokenType type, String lexeme, int location){
        if (type == TokenType.BLOCK_COMMENT || type==TokenType.INVALID_COMMENT) {
            // remove /n outside /*..*/
            if (lexeme.indexOf('\n') < lexeme.indexOf('/')){
                for (int i = lexeme.indexOf('\n'); i<=lexeme.indexOf('/');i++){
                    lexeme = lexeme.replaceFirst("\n","");
                }
            }
            location = location - lexeme.split("\n").length + 1;
            lexeme = lexeme.replaceAll("\r", "\\\\r");
            lexeme = lexeme.replaceAll("\n", "\\\\n");
        }
        else {
            lexeme = lexeme.replaceAll("\n", "");
            lexeme = lexeme.replaceAll("\r", "");
            lexeme = lexeme.replaceAll("\t", "");
        }

        if (type==TokenType.STRING_LITERAL || type==TokenType.INVALID_STRING){
            lexeme = lexeme.replaceAll("\"","");
        }
        else if (type != TokenType.INLINE_COMMENT &&
                type != TokenType.BLOCK_COMMENT &&
                type!=TokenType.INVALID_COMMENT){
            lexeme = lexeme.replaceAll(" ", "");
        }

        this.lexeme = lexeme;
        this.location = location;
        this.type = type;
    }

    public static TokenType convertToErrorType(TokenType type){
        if (type == TokenType.ID) {
            type = TokenType.INVALID_IDENTIFIER;
        }
        else if (type == TokenType.INTEGER_NUMBER || type == TokenType.FLOAT_NUMBER){
            type = TokenType.INVALID_NUMBER;
        }
        else {
            type = TokenType.INVALID_CHARACTER;
        }
        return type;
    }

    @Override
    public String toString(){
        return "["+type+", "+lexeme+", "+location+"]";
    }
    public int getLocation() { return location; }
    public TokenType getType() { return type; }

}
