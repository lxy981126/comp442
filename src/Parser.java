import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    Grammar grammar;
    HashMap<String, ArrayList<String>> firstSet;
    HashMap<String, ArrayList<String>> followSet;
    HashMap<String, HashMap<String, Production>> parsingTable;

    public Parser() {
        try {
            this.grammar = new Grammar("./grm/non_ambiguous.grm");
            this.firstSet = buildFirstFollowSet("grm/non_ambiguous.grm.first");
            addTerminalsToFirstSet();
            this.followSet = buildFirstFollowSet("grm/non_ambiguous.grm.follow");
            buildParsingTable();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addTerminalsToFirstSet() {
        for (Terminal terminal:grammar.terminals) {
            ArrayList<String> list = new ArrayList<>();
            list.add(terminal.name);
            firstSet.put(terminal.name, list);
        }

        ArrayList<String> list = new ArrayList<>();
        list.add("EPSILON");
        firstSet.put("EPSILON", list);
    }

    public HashMap<String, ArrayList<String>> buildFirstFollowSet(String file)
            throws FileNotFoundException{
        HashMap<String, ArrayList<String>> set= new HashMap<>();
        Scanner scanner = new Scanner(new File(file));

        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("FIRST") || line.contains("FOLLOW")) {
                int leftBracket = line.indexOf("<");
                int rightBracket = line.indexOf(">");
                String name = line.substring(leftBracket, rightBracket + 1);
                NonTerminal nonTerminal = new NonTerminal(name);

                ArrayList<String> arrayToAdd = set.get(nonTerminal.name);
                if (arrayToAdd == null) {
                    arrayToAdd = new ArrayList<>();
                    set.put(nonTerminal.name, arrayToAdd);
                }

                int leftSquare = line.indexOf("[");
                int rightSquare = line.indexOf("]");
                String terminalsName = line.substring(leftSquare, rightSquare + 1);
                terminalsName = terminalsName.replaceAll("\\[", "").replaceAll("\\]","");
                String[] terminals = terminalsName.replaceAll(" ", "").split(",");

                for (String terminal:terminals) {
                    if (terminal.equals("EPSILON")) {
                        arrayToAdd.add("EPSILON");
                    }
                    else {
                        arrayToAdd.add(terminal);
                    }
                }

            }
        }
        return set;
    }

    public void buildParsingTable() {
        parsingTable = new HashMap<>();
        ArrayList<Production> productions = grammar.productions;

        for (Production production:productions) {
            NonTerminal nonTerminal = production.lhs;
            Symbol rhsSymbol = production.rhs.get(0);

            HashMap<String, Production> row = parsingTable.get(nonTerminal.name);
            if (row == null) {
                row = new HashMap<>();
            }

            ArrayList<Terminal> terminals = grammar.terminals;
            for (Terminal terminal:terminals) {
                ArrayList<String> first = firstSet.get(rhsSymbol.name);

                if (first.contains(terminal.name)) {
                    row.put(terminal.name, production);
                }
            }
            parsingTable.put(nonTerminal.name, row);
        }
    }

}
