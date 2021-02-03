import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyser {
    private int lineCounter;
    private Integer backup;
    private FileReader reader;
    private State initial;

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

                current = current.getNextState(nextChar);
                if (current == null){
                    return token;
                }
                else if (current != initial){
                    lexeme += String.valueOf(nextChar);
                }

                if (current.isFinal()){
                    int line = lineCounter - (lexeme.split("\n").length-1);
                    token = new Token(current.getOutputToken(), lexeme, line);
                    backup = null;
                }

                if (current.hasTransition()){
                    next = reader.read();
                    backup = next;
                    continue;
                }

                if (token != null){
                    break;
                }
                next = reader.read();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return token;
    }

}
