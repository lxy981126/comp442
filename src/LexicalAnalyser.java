import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyser {
    private int lineCounter;
    private Integer backup;
    final private FileReader reader;
    final private State initial;

    public LexicalAnalyser(String input) throws FileNotFoundException {
        lineCounter = 1;
        backup = null;
        reader = new FileReader(input);
        initial = DFA.constructDFA();
    }

    public Token nextToken(){
        Token token = null;
        String lexeme = "";
        State current = initial;
        try {
            int next;
            if (backup != null){
                next = backup;
                if(backup=='\n'){
                    lineCounter--;
                }
            }
            else {
                next = reader.read();
            }
            while (next != -1) {
                char nextChar = (char) next;
                if (nextChar == '\n'){
                    lineCounter++;
                }

                lexeme += String.valueOf(nextChar);
                current = current.getNextState(nextChar);
                if (current == null){
                    if (token==null){
                        backup = null;
                        return new Token(TokenType.INVALID_CHARACTER, String.valueOf(nextChar), lineCounter);
                    }
                    return token;
                }

                if (current.isFinal()){
                    token = new Token(current.getOutputToken(), lexeme, lineCounter);
                    backup = null;
                }
                else {
                }

                if (current.hasTransition()){
                    next = reader.read();
                    backup = next;
                    continue;
                }

                if (!current.isFinal() && !current.hasTransition()){
//                    backup = null;
                    return new Token(Token.convertToErrorType(token.getType()), lexeme, lineCounter);
                }

                if (token != null){
                    break;
                }
//                backup = next;
                next = reader.read();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return token;
    }

}
