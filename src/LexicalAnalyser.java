import java.io.*;

public class LexicalAnalyser {
    private int lineCounter;
    private Integer backup;
    private String outputToken;
    private String errorToken;
    private String input;
    final private FileReader reader;
    final private State initial;

    public LexicalAnalyser(String input) throws FileNotFoundException {
        lineCounter = 1;
        backup = null;
        reader = new FileReader(input);
        initial = DFA.constructDFA();
        this.input = input.replaceAll(".src","");
        this.outputToken = "";
        this.errorToken = "";
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
                    prepareOutputToken(token);
                    return token;
                }
                else if (current!=initial){
                    token = new Token(current.getOutputToken(), lexeme, lineCounter);
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

            if (next == -1 && token == null) {
                token = new Token(TokenType.END_OF_FILE, "$", lineCounter);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        prepareOutputToken(token);
        return token;
    }

    public void outputStream() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("out/" + input + ".outlextokens"));
            writer.write(outputToken);
            writer.close();

            BufferedWriter errorWriter = new BufferedWriter(new FileWriter("out/" + input + ".outlexerrors"));
            errorWriter.write(errorToken);
            errorWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareOutputToken(Token token) {
        if (token == null) {
            return;
        }
        if (token.getType()==TokenType.INVALID_CHARACTER ||
                token.getType()==TokenType.INVALID_IDENTIFIER ||
                token.getType()==TokenType.INVALID_NUMBER){
            errorToken += "Lexical error: "+token.toString()+"\n";
        }
        else {
            outputToken += token + "\n";
        }
    }
}
