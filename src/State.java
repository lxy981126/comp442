import java.util.HashMap;

public class State {
    private HashMap<Character, State> nextStates;
    private HashMap<Character, State> backtrace;
    private boolean isFinal;
    private TokenType outputToken;

    public State(boolean isFinal, TokenType outputToken) {
        this.nextStates = new HashMap<Character, State>();
        this.backtrace = new HashMap<Character, State>();
        this.isFinal = isFinal;
        this.outputToken = outputToken;
    }

    public void addChild(Character transition, State child) {
        nextStates.put(transition, child);
    }

    public State getNextState(Character character){
        return nextStates.get(character);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public TokenType getOutputToken() {
        return outputToken;
    }

}
