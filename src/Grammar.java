import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Grammar {
    ArrayList<Terminal> terminals;
    ArrayList<NonTerminal> nonTerminals;
    Terminal startingSymbol;
    ArrayList<Production> productions;

    public Grammar(String grammarFile) throws FileNotFoundException {
        HashMap<String, Terminal> terminalHashMap = new HashMap<>();
        HashMap<String, NonTerminal> nonTerminalHashMap = new HashMap<>();
        productions = new ArrayList<>();

        Scanner scanner = new Scanner(new File(grammarFile));
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] symbols = line.split(" ");
            Production production = null;
            boolean lhs = true;

            for (String symbol:symbols) {
                if (symbol.contains("<") && symbol.contains(">")) {
                    NonTerminal nonTerminal = nonTerminalHashMap.get(symbol);
                    if (nonTerminal == null) {
                        nonTerminal = new NonTerminal(symbol);
                        nonTerminalHashMap.put(symbol, nonTerminal);
                    }

                    if (lhs) {
                        production = new Production(nonTerminal);
                    }
                    else {
                        production.addRHS(nonTerminal);
                    }
                }
                else if (symbol.contains("'")) {
                    Terminal terminal = terminalHashMap.get(symbol);
                    if (terminal == null) {
                        terminal = new Terminal(symbol);
                        terminalHashMap.put(symbol, terminal);
                    }
                    production.addRHS(terminal);
                }
                else if (symbol.contains("::=")) {
                    lhs = false;
                }
                else if (symbol.contains("EPSILON")) {
                    production.addRHS(new Epsilon());
                }
            }

            if (production != null) {
                productions.add(production);
            }
        }
        terminals = new ArrayList<>(terminalHashMap.values());
        nonTerminals = new ArrayList<>(nonTerminalHashMap.values());
        startingSymbol = terminalHashMap.get("START");
    }


}
