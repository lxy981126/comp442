import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Parser {
    Grammar grammar;
    HashMap<String, ArrayList<String>> firstSet;
    HashMap<String, ArrayList<String>> followSet;
    HashMap<String, HashMap<String, Production>> parsingTable;
    Stack<String> stack;

    public Parser() {
        try {
            stack = new Stack();
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

    public void parse(String inputFile) throws FileNotFoundException {
        LexicalAnalyser analyser = new LexicalAnalyser(inputFile);

        stack.push("$");
        stack.push("<START>");

        Token nextToken = analyser.nextToken();
        while (stack.firstElement().equals("$")) {
            String top = stack.firstElement();

            if (grammar.terminals.contains(top)) {
                if (top.equals(nextToken.toString())) {
                    stack.pop();
                    nextToken = analyser.nextToken();
                }
                else {
                    // TODO:function skipErrors()
                }
            }
        }
    }

    public void addTerminalsToFirstSet() {
        for (String terminal:grammar.terminals) {
            ArrayList<String> list = new ArrayList<>();
            list.add(terminal);
            firstSet.put(terminal, list);
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
                String nonTerminal = line.substring(leftBracket, rightBracket + 1);

                ArrayList<String> arrayToAdd = set.get(nonTerminal);
                if (arrayToAdd == null) {
                    arrayToAdd = new ArrayList<>();
                    set.put(nonTerminal, arrayToAdd);
                }

                int leftSquare = line.indexOf("[");
                int rightSquare = line.indexOf("]");
                String terminalsName = line.substring(leftSquare + 1, rightSquare);
                String[] terminals = terminalsName.split(", ");

                for (String terminal:terminals) {
                    arrayToAdd.add(terminal);
                }

            }
        }
        return set;
    }

    public void buildParsingTable() {
        parsingTable = new HashMap<>();
        ArrayList<Production> productions = grammar.productions;

        for (Production production:productions) {
            String nonTerminal = production.lhs;
            String rhsSymbol = production.rhs.get(0);

            HashMap<String, Production> row = parsingTable.get(nonTerminal);
            if (row == null) {
                row = new HashMap<>();
                parsingTable.put(nonTerminal, row);
            }

            ArrayList<String> terminals = grammar.terminals;
            for (String terminal:terminals) {
                ArrayList<String> first = firstSet.get(rhsSymbol);

                if (first.contains(terminal)) {
                    row.put(terminal, production);
                }
                else {
                    // TODO error
                }
            }
        }
    }

}
