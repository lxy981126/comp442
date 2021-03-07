import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Grammar {
    ArrayList<String> terminals;
    ArrayList<String> nonTerminals;
    String startingSymbol;
    ArrayList<Production> productions;

    public Grammar(String grammarFile) throws FileNotFoundException {
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        productions = new ArrayList<>();

        Scanner scanner = new Scanner(new File(grammarFile));
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] symbols = line.split(" ");
            Production production = null;
            boolean lhs = true;

            for (String symbol:symbols) {
                if (symbol.contains("<") && symbol.contains(">")) {
                    nonTerminals.add(symbol);
                    if (lhs) {
                        production = new Production(symbol);
                    }
                    else {
                        production.addRHS(symbol);
                    }
                }
                else if (symbol.contains("'")) {
                    terminals.add(symbol);
                    production.addRHS(symbol);
                }
                else if (symbol.contains("::=")) {
                    lhs = false;
                }
                else if (symbol.contains("EPSILON")) {
                    production.addRHS(symbol);
                }
            }

            if (production != null) {
                productions.add(production);
            }
        }
        startingSymbol = "<START>";
    }

}
