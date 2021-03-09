import java.io.*;
import java.util.*;

public class Parser {
    Grammar grammar;
    HashMap<String, ArrayList<String>> firstSet;
    HashMap<String, ArrayList<String>> followSet;
    HashMap<String, HashMap<String, Production>> parsingTable;
    Stack<String> parsingStack;
    LexicalAnalyser analyser;

    public Parser(String inputFile) {
        try {
            parsingStack = new Stack();
            analyser = new LexicalAnalyser(inputFile);
            this.grammar = new Grammar("./grm/non_ambiguous_LL1.grm");
            this.firstSet = buildFirstFollowSet("grm/non_ambiguous_LL1.grm.first");
            addTerminalsToFirstSet();
            this.followSet = buildFirstFollowSet("grm/non_ambiguous_LL1.grm.follow");
            addTerminalsToFollowSet();
            buildParsingTable();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean parse() {
        boolean error = false;

        parsingStack.push("$");
        parsingStack.push(grammar.startingSymbol);

        Token nextToken = analyser.nextToken();
        while (!parsingStack.peek().equals("$") && nextToken != null) {
            String top = parsingStack.peek();

            if (nextToken.getType() == TokenType.BLOCK_COMMENT || nextToken.getType() == TokenType.INLINE_COMMENT) {
                nextToken = analyser.nextToken();
            }

            if (grammar.terminals.contains(top)) {
                if (top.equals(nextToken.getType().toString()) || top.equals(nextToken.lexeme)) {
                    parsingStack.pop();
                    nextToken = analyser.nextToken();
                }
                else {
                    skipError(nextToken);
                    error = true;
                }
            }
            else {
                HashMap<String, Production> row = parsingTable.get(top);

                Production production = row.get(nextToken.lexeme) == null ?
                        row.get(nextToken.getType().toString()) : row.get(nextToken.lexeme);

                if (production != null) {
                    System.out.println(production);
                    parsingStack.pop();
                    inverseRHSMultiplePush(production);
                }
                else {
                    skipError(nextToken);
                    error = true;
                }
            }
        }

        if (!parsingStack.peek().equals("$") || error == true) {
            return false;
        }
        else {
            return true;
        }
    }

    private void skipError(Token nextToken) {
       System.err.println("Syntax error at line " + nextToken.location + ": expected \"" + parsingStack.peek() +
               "\", got \"" + nextToken.lexeme+"\'");
       ArrayList<String> follow = followSet.get(parsingStack.peek());
       ArrayList<String> first = firstSet.get(parsingStack.peek());

       if ((!follow.contains(nextToken.lexeme) && !follow.contains(nextToken.getType().toString()))) {
           parsingStack.pop();
       }
       else {
           while (!(first.contains(nextToken.getType().toString()) &&
                   !first.contains(nextToken.lexeme)) ||
                   (first.contains("EPSILON")
                           && (!follow.contains(nextToken.getType().toString())
                           && !follow.contains(nextToken.lexeme)))) {
               nextToken = analyser.nextToken();
           }
       }
    }

    private void inverseRHSMultiplePush(Production production) {
        ArrayList<String> rhs = production.rhs;
        for (int i = rhs.size() - 1; i >= 0 ; i--) {
            String symbol = rhs.get(i);
            if (!symbol.equals("EPSILON")) {
                parsingStack.push(symbol);
            }
        }
    }

    private void addTerminalsToFirstSet() {
        for (String terminal:grammar.terminals) {
            ArrayList<String> list = new ArrayList<>();
            list.add(terminal);
            firstSet.put(terminal, list);
        }

        ArrayList<String> list = new ArrayList<>();
        list.add("EPSILON");
        firstSet.put("EPSILON", list);
    }

    private void addTerminalsToFollowSet() {
        for (String terminal:grammar.terminals) {
            followSet.put(terminal, new ArrayList<>());
        }
        followSet.put("EP", new ArrayList<>());
    }

    private HashMap<String, ArrayList<String>> buildFirstFollowSet(String file)
            throws FileNotFoundException{
        HashMap<String, ArrayList<String>> set= new HashMap<>();
        Scanner scanner = new Scanner(new File(file));

        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("FIRST") || line.contains("FOLLOW")) {
                int leftBracket = line.indexOf("<");
                int rightBracket = line.indexOf(">");
                String nonTerminal = line.substring(leftBracket + 1, rightBracket);

                ArrayList<String> arrayToAdd = set.get(nonTerminal);
                if (arrayToAdd == null) {
                    arrayToAdd = new ArrayList<>();
                    set.put(nonTerminal, arrayToAdd);
                }

                int leftSquare = line.indexOf("[");
                int rightSquare = line.lastIndexOf("]");
                String terminalsName = line.substring(leftSquare + 1, rightSquare);
                String[] terminals = terminalsName.split(", ");

                for (String terminal:terminals) {
                    if (terminal.contains("'"))
                    {
                        terminal = terminal.substring(1, terminal.length() - 1);
                    }
                    arrayToAdd.add(terminal);
                }

            }
        }
        return set;
    }

    private void buildParsingTable() {
        parsingTable = new HashMap<>();
        ArrayList<Production> productions = grammar.productions;

        for (Production production:productions) {
            String nonTerminal = production.lhs;

            HashMap<String, Production> row = parsingTable.get(nonTerminal);
            if (row == null) {
                row = new HashMap<>();
                parsingTable.put(nonTerminal, row);
            }

            ArrayList<String> first = getFirstSet(production);
            ArrayList<String> terminals = grammar.terminals;
            for (String terminal:terminals) {
                if (first.contains(terminal)) {
                    row.put(terminal, production);
                }
            }

            if (first.contains("EPSILON")) {
                ArrayList<String> follow = followSet.get(nonTerminal);
                for (String terminal:terminals) {
                    if (follow.contains(terminal)) {
                        row.put(terminal, production);
                    }
                }
            }

            for (String terminal:terminals) {
                if (row.get(terminal) == null) {
                    row.put(terminal, null);
                }
            }
        }
        outputParsingTableToFile();
    }

    private ArrayList<String> getFirstSet(Production production) {
        ArrayList<String> first = new ArrayList<>();

        int i = 0;
        String rhsSymbol;
        ArrayList<String> firstSetRhsSymbol;

        do {
            rhsSymbol = production.rhs.get(i);
            firstSetRhsSymbol = firstSet.get(rhsSymbol);
            for (String symbol:firstSetRhsSymbol) {
                if (!first.contains(symbol)) {
                    first.add(symbol);
                }
            }
            i++;
        }while (firstSetRhsSymbol.contains("EPSILON") && i<production.rhs.size());
        return first;
    }

    private void outputParsingTableToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("out/ParsingTable.tsv"));

            writer.write("\t");
            for (String header:grammar.terminals) {
                writer.write(header+"\t");
            }
            writer.write("\n");


            for (Map.Entry row:parsingTable.entrySet()) {
                writer.write(row.getKey().toString()+"\t");

                for (String header:grammar.terminals) {
                    HashMap<String, Production> values = (HashMap<String, Production>)row.getValue();
                    writer.write(values.get(header)+"\t");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
