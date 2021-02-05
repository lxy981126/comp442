import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String input = "./test/lexpositivegrading";
        FileWriter outTokens = new FileWriter(input+"_.outlextokens");
        FileWriter outErrors = new FileWriter(input+"_.outlexerrors");

        LexicalAnalyser analyser = new LexicalAnalyser(input+".src");

        while (true){
            Token token = analyser.nextToken();
            if (token == null){
                break;
            }
            else {
                outTokens.write(token.toString());
                System.out.println(token.toString());
            }
        }
        outTokens.close();
        outErrors.close();
    }
}
