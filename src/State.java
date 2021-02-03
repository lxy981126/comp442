import java.util.Arrays;
import java.util.HashMap;

public class State {
    private HashMap<Character, State> nextStates;
    private boolean isFinal;
    private TokenType outputToken;

    public State(boolean isFinal, TokenType outputToken) {
        this.nextStates = new HashMap<Character, State>();
        this.isFinal = isFinal;
        this.outputToken = outputToken;
    }

    public void addTransition(Character transition, State child) {
        nextStates.put(transition, child);
    }

    public void addTransitions(char fromChar, char toChar, State child) {
        for (int i=(int)fromChar; i<=(int)toChar; i++){
            addTransition((char)i, child);
        }
    }

    public void addTransitionsWithException(char fromChar, char toChar, State child, Character[] exceptions) {
        for (int i=(int)fromChar; i<=(int)toChar; i++){
            if (Arrays.asList(exceptions).contains((char)i) == false){
                addTransition((char)i, child);
            }
        }
    }

    public boolean isFinal() { return isFinal; }
    public boolean hasTransition() { return !nextStates.isEmpty(); }
    public TokenType getOutputToken() {
        return outputToken;
    }
    public State getNextState(Character character) { return nextStates.get(character); }

}
