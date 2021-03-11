import java.io.*;
import java.util.*;

public class Parser {
    Grammar grammar;
    HashMap<SyntaxSymbol, ArrayList<SyntaxSymbol>> firstSet;
    HashMap<SyntaxSymbol, ArrayList<SyntaxSymbol>> followSet;
    HashMap<SyntaxSymbol, HashMap<SyntaxSymbol, Production>> parsingTable;
    Stack<Symbol> parsingStack;
    Stack<ASTNode> semanticStack;
    LexicalAnalyser analyser;

    public Parser(String inputFile) {
        try {
            parsingStack = new Stack<>();
            semanticStack = new Stack<>();
            analyser = new LexicalAnalyser(inputFile);
            this.grammar = new Grammar("./grm/LL1_sdt.grm");
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

        parsingStack.push(new SyntaxSymbol("$", SyntaxSymbolType.END_OF_FILE));
        parsingStack.push(grammar.startingSymbol);

        Token nextToken = analyser.nextToken();
        while ((parsingStack.peek() instanceof SemanticSymbol ||
                ((SyntaxSymbol) parsingStack.peek()).type != SyntaxSymbolType.END_OF_FILE)) {
            Symbol top = parsingStack.peek();

            if (top instanceof SemanticSymbol) {
                SemanticAction.performAction(semanticStack, (SemanticSymbol) top, nextToken);
                parsingStack.pop();
            }
            else if (nextToken != null) {
                if (nextToken.getType() == TokenType.BLOCK_COMMENT || nextToken.getType() == TokenType.INLINE_COMMENT) {
                    nextToken = analyser.nextToken();
                }

                SyntaxSymbol topSyntax = (SyntaxSymbol) top;
                if (topSyntax.type == SyntaxSymbolType.TERMINAL) {
                    if (topSyntax.equals(nextToken)) {
                        parsingStack.pop();
                        nextToken = analyser.nextToken();
                    }
                    else {
                        skipError(nextToken);
                        error = true;
                    }
                }
                else {
                    HashMap<SyntaxSymbol, Production> row = parsingTable.get(topSyntax);
                    SyntaxSymbol lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
                    SyntaxSymbol type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);

                    Production production = row.get(lexeme) == null ? row.get(type) : row.get(lexeme);
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
        }

        System.out.println(semanticStack.peek().toString());
        if ((parsingStack.peek() instanceof SyntaxSymbol &&
                ((SyntaxSymbol) parsingStack.peek()).type != SyntaxSymbolType.END_OF_FILE) ||
                error == true) {
            return false;
        }
        else {
            return true;
        }
    }

    private void skipError(Token nextToken) {
       System.err.println("Syntax error at line " + nextToken.location + ": expected \"" + parsingStack.peek() +
               "\", got \"" + nextToken.lexeme+"\'");
       ArrayList<SyntaxSymbol> follow = followSet.get(parsingStack.peek());
       ArrayList<SyntaxSymbol> first = firstSet.get(parsingStack.peek());
       SyntaxSymbol lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
       SyntaxSymbol type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);

       if (!follow.contains(lexeme) && !follow.contains(type)) {
           parsingStack.pop();
       }
       else {
           while ((!first.contains(lexeme) && !first.contains(type)) ||
                   (first.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON)) &&
                           !follow.contains(lexeme) &&
                           !follow.contains(type))) {
               nextToken = analyser.nextToken();
               lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
               type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);
           }
       }
    }

    private void inverseRHSMultiplePush(Production production) {
        ArrayList<Symbol> rhs = production.rhs;

        for (int i = rhs.size() - 1; i >= 0 ; i--) {
            Symbol symbol = rhs.get(i);

            if (!symbol.equals(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON))) {
                parsingStack.push(symbol);
            }
        }
    }

    private void addTerminalsToFirstSet() {
        for (SyntaxSymbol terminal:grammar.terminals) {
            ArrayList<SyntaxSymbol> list = new ArrayList<>();
            list.add(terminal);
            firstSet.put(terminal, list);
        }

        ArrayList<SyntaxSymbol> list = new ArrayList<>();
        SyntaxSymbol epsilon = new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON);
        list.add(epsilon);
        firstSet.put(epsilon, list);
    }

    private void addTerminalsToFollowSet() {
        for (SyntaxSymbol terminal:grammar.terminals) {
            followSet.put(terminal, new ArrayList<>());
        }
        followSet.put(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON), new ArrayList<>());
    }

    private HashMap<SyntaxSymbol, ArrayList<SyntaxSymbol>> buildFirstFollowSet(String file)
            throws FileNotFoundException{
        HashMap<SyntaxSymbol, ArrayList<SyntaxSymbol>> set= new HashMap<>();
        Scanner scanner = new Scanner(new File(file));

        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("FIRST") || line.contains("FOLLOW")) {
                int leftBracket = line.indexOf("<");
                int rightBracket = line.indexOf(">");
                String nonTerminal = line.substring(leftBracket + 1, rightBracket);
                SyntaxSymbol syntaxSymbol = new SyntaxSymbol(nonTerminal, SyntaxSymbolType.NON_TERMINAL);

                ArrayList<SyntaxSymbol> arrayToAdd = set.get(syntaxSymbol);
                if (arrayToAdd == null) {
                    arrayToAdd = new ArrayList<>();
                    set.put(syntaxSymbol, arrayToAdd);
                }

                int leftSquare = line.indexOf("[");
                int rightSquare = line.lastIndexOf("]");
                String terminalsName = line.substring(leftSquare + 1, rightSquare);
                String[] terminals = terminalsName.split(", ");

                for (String terminalName:terminals) {
                    if (terminalName.contains("EPSILON"))
                    {
                        arrayToAdd.add(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON));
                    }
                    else {
                        terminalName = terminalName.substring(1, terminalName.length() - 1);
                        SyntaxSymbol terminal = new SyntaxSymbol(terminalName, SyntaxSymbolType.TERMINAL);
                        arrayToAdd.add(terminal);
                    }
                }

            }
        }
        return set;
    }

    private void buildParsingTable() {
        parsingTable = new HashMap<>();
        ArrayList<Production> productions = grammar.productions;

        for (Production production:productions) {
            SyntaxSymbol nonTerminal = production.lhs;

            HashMap<SyntaxSymbol, Production> row = parsingTable.get(nonTerminal);
            if (row == null) {
                row = new HashMap<>();
                parsingTable.put(nonTerminal, row);
            }

            ArrayList<SyntaxSymbol> first = getFirstSet(production);
            ArrayList<SyntaxSymbol> terminals = grammar.terminals;
            for (SyntaxSymbol terminal:terminals) {
                if (first.contains(terminal)) {
                    row.put(terminal, production);
                }
            }

            if (first.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON))) {
                ArrayList<SyntaxSymbol> follow = followSet.get(nonTerminal);
                for (SyntaxSymbol terminal:terminals) {
                    if (follow.contains(terminal)) {
                        row.put(terminal, production);
                    }
                }
            }

            for (SyntaxSymbol terminal:terminals) {
                if (row.get(terminal) == null) {
                    row.put(terminal, null);
                }
            }
        }
        outputParsingTableToFile();
    }

    private ArrayList<SyntaxSymbol> getFirstSet(Production production) {
        ArrayList<SyntaxSymbol> first = new ArrayList<>();

        int i = 0;
        Symbol rhsSymbol;
        ArrayList<SyntaxSymbol> firstSetRhsSymbol = new ArrayList<>();

        do {
            rhsSymbol = production.rhs.get(i);

            if (rhsSymbol instanceof SyntaxSymbol) {
                firstSetRhsSymbol = firstSet.get(rhsSymbol);

                for (SyntaxSymbol symbol:firstSetRhsSymbol) {
                    if (!first.contains(symbol)) {
                        first.add(symbol);
                    }
                }
            }
            i++;
        }while (firstSetRhsSymbol.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON)) &&
                i<production.rhs.size());
        return first;
    }

    private void outputParsingTableToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("out/ParsingTable.tsv"));

            writer.write("\t");
            for (SyntaxSymbol header:grammar.terminals) {
                writer.write(header+"\t");
            }
            writer.write("\n");


            for (Map.Entry row:parsingTable.entrySet()) {
                writer.write(row.getKey().toString()+"\t");

                for (SyntaxSymbol header:grammar.terminals) {
                    HashMap<SyntaxSymbol, Production> values = (HashMap<SyntaxSymbol, Production>)row.getValue();
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
