import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyser {
    private int lineCounter = 1;
    private FileReader reader;
    private State initial;

    public LexicalAnalyser(String input) throws FileNotFoundException {
        initial = DFA.constructDFA();
        reader = new FileReader(input);
    }

    public Token nextToken(){
        Token token = null;
        String lexeme = "";
        State current = initial;
        try {
            int next = reader.read();
            while (next != -1) {
                char nextChar = (char) next;
                lexeme += String.valueOf(nextChar);

                current = current.getNextState(nextChar);
                if (current.isFinal()){
                    token = new Token(current.getOutputToken(), lexeme, lineCounter);
                    System.out.println(token);
                    break;
                }
                next = reader.read();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return token;
    }

    public boolean isNonzero(char character){
        if (Integer.parseInt(String.valueOf(character)) == 0){
            return false;
        }
        else {
            return true;
        }
    }
}
