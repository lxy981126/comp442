import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Grammar {
    ArrayList<SyntaxSymbol> terminals;
    ArrayList<SyntaxSymbol> nonTerminals;
    SyntaxSymbol startingSymbol;
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
                    symbol = symbol.substring(1, symbol.length() - 1);
                    SyntaxSymbol syntaxSymbol = new SyntaxSymbol(symbol, SyntaxSymbolType.NON_TERMINAL);

                    if (!nonTerminals.contains(syntaxSymbol)) {
                        nonTerminals.add(syntaxSymbol);
                    }

                    if (lhs) {
                        production = new Production(syntaxSymbol);
                    }
                    else {
                        production.addRHS(syntaxSymbol);
                    }
                }
                else if (symbol.contains("'")) {
                    symbol = symbol.substring(1, symbol.length() - 1);
                    SyntaxSymbol syntaxSymbol = new SyntaxSymbol(symbol, SyntaxSymbolType.TERMINAL);

                    if (!terminals.contains(syntaxSymbol)) {
                        terminals.add(syntaxSymbol);
                    }
                    production.addRHS(syntaxSymbol);
                }
                else if (symbol.contains("#")) {
                    symbol = symbol.substring(1, symbol.length() - 1);
                    SemanticSymbol semanticSymbol = new SemanticSymbol(symbol);
                    production.addRHS(semanticSymbol);
                }
                else if (symbol.contains("::=")) {
                    lhs = false;
                }
                else if (symbol.contains("EPSILON")) {
                    production.addRHS(new SyntaxSymbol(symbol, SyntaxSymbolType.EPSILON));
                }
            }

            if (production != null) {
                productions.add(production);
            }
        }
        startingSymbol = new SyntaxSymbol("START", SyntaxSymbolType.NON_TERMINAL);
    }

}
