import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static int line = 1;
    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter a file name");
//        String input = scanner.nextLine().replaceAll(".src", "");
//
//        FileWriter outTokens = new FileWriter(input+".outlextokens");
//        FileWriter outErrors = new FileWriter(input+".outlexerrors");
//
//        LexicalAnalyser analyser = new LexicalAnalyser(input+".src");
//
//        while (true){
//            Token token = analyser.nextToken();
//            if (token == null) {
//                break;
//            }
//            else {
//                String formatted = "";
//                if (token.getLocation()>line){
//                    formatted +="\n";
//                    line = token.getLocation();
//                }
//                formatted += token.toString();
//                outTokens.write(formatted+" ");
//                System.out.print(formatted+" ");
//            }
//            if (token.getType()==TokenType.INVALID_CHARACTER ||
//                    token.getType()==TokenType.INVALID_IDENTIFIER ||
//                    token.getType()==TokenType.INVALID_NUMBER){
//                outErrors.write("Lexical error: "+token.toString()+"\n");
//            }
//        }
//        outTokens.close();
//        outErrors.close();

//        // first set test
//        Grammar g = new Grammar("./grm/non_ambiguous.grm");
//        HashMap<NonTerminal, ArrayList<Symbol>> first = Grammar.buildFirstFollowSet("grm/non_ambiguous.grm.first");
//        for (HashMap.Entry entry:first.entrySet()) {
//            String str = entry.getKey() + ": {";
//            for (Symbol symbol:(ArrayList<Symbol>)entry.getValue()) {
//                str += symbol + ", ";
//            }
//            System.out.println(str + "}");
//        }

        // follow set test
        HashMap<NonTerminal, ArrayList<Symbol>> follow = Grammar.buildFirstFollowSet("grm/non_ambiguous.grm.follow");
        for (HashMap.Entry entry:follow.entrySet()) {
            String str = entry.getKey() + ": {";
            for (Symbol symbol:(ArrayList<Symbol>)entry.getValue()) {
                str += symbol + ", ";
            }
            System.out.println(str + "}");
        }
    }

}
