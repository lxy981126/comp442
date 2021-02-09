import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static int line = 1;
    public static void main(String[] args) throws IOException {
        String input = "./test/test";
        FileWriter outTokens = new FileWriter(input+"_.outlextokens");
        FileWriter outErrors = new FileWriter(input+"_.outlexerrors");

        LexicalAnalyser analyser = new LexicalAnalyser(input+".src");

        while (true){
            Token token = analyser.nextToken();
            if (token == null) {
                break;
            }
            else {
                String formatted = "";
                if (token.getLocation()>line){
                    formatted +="\n";
                    line = token.getLocation();
                }
                formatted += token.toString();
                outTokens.write(formatted+" ");
                System.out.print(formatted+" ");
            }
            if (token.getType()==TokenType.INVALID_CHARACTER ||
                    token.getType()==TokenType.INVALID_IDENTIFIER ||
                    token.getType()==TokenType.INVALID_NUMBER){
                outErrors.write("Lexical error: "+token.toString()+"\n");
            }
        }
        outTokens.close();
        outErrors.close();
    }

}
