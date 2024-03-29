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
    BufferedWriter errorWriter;
    String inputFile;

    public Parser(String inputFile) {
        try {
            this.inputFile = inputFile;
            this.errorWriter = new BufferedWriter
                    (new FileWriter("out/" + inputFile + ".outsyntaxerrors", false));
            parsingStack = new Stack<>();
            semanticStack = new Stack<>();
            analyser = new LexicalAnalyser(inputFile + ".src");
            this.grammar = new Grammar("./grm/LL1_sdt.grm");
            this.firstSet = buildFirstFollowSet("grm/non_ambiguous_LL1.grm.first");
            addTerminalsToFirstSet();
            this.followSet = buildFirstFollowSet("grm/non_ambiguous_LL1.grm.follow");
            addTerminalsToFollowSet();
            buildParsingTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean parse() throws IOException {
        BufferedWriter derivationWriter = new BufferedWriter
                (new FileWriter("out/" + inputFile + ".outderivation"));
        BufferedWriter astWriter = new BufferedWriter
                (new FileWriter("out/" + inputFile + ".outast"));
        BufferedWriter dotWriter = new BufferedWriter
                (new FileWriter("out/" + inputFile + ".dot"));

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
            else {
                if (nextToken.getType() == TokenType.BLOCK_COMMENT || nextToken.getType() == TokenType.INLINE_COMMENT) {
                    nextToken = analyser.nextToken();
                    continue;
                }

                SyntaxSymbol topSyntax = (SyntaxSymbol) top;
                if (topSyntax.type == SyntaxSymbolType.TERMINAL) {
                    if (topSyntax.equals(nextToken)) {
                        parsingStack.pop();
                        nextToken = analyser.nextToken();
                    }
                    else {
                        nextToken = skipError(nextToken);
                        error = true;
                    }
                }
                else {
                    HashMap<SyntaxSymbol, Production> row = parsingTable.get(topSyntax);
                    SyntaxSymbol lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
                    SyntaxSymbol type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);

                    Production production = row.get(lexeme) == null ? row.get(type) : row.get(lexeme);
                    if (production != null) {
                        derivationWriter.write(production.toString() + "\n");
                        parsingStack.pop();
                        inverseRHSMultiplePush(production);
                    }
                    else {
                        nextToken = skipError(nextToken);
                        error = true;
                    }
                }
            }
        }
        analyser.outputStream();

        astWriter.write("graph ast {\n");
        dotWriter.write("graph ast {\n");

        ASTNode currentNode = semanticStack.empty()? null:semanticStack.peek();
        astWriter.write(currentNode.toString());
        dotWriter.write(currentNode.toString());

        astWriter.write("}");
        dotWriter.write("}");

        derivationWriter.close();
        astWriter.close();
        dotWriter.close();

        semanticProcessing(currentNode);

        if (!(parsingStack.peek() instanceof SyntaxSymbol &&
                ((SyntaxSymbol) parsingStack.peek()).type == SyntaxSymbolType.END_OF_FILE) ||
                error == true) {
            return false;
        }
        else {
            return true;
        }
    }

    private Token skipError(Token nextToken) throws IOException {
        String errorMessage = "Syntax error at line " + nextToken.location + ": expected \"" + parsingStack.peek() +
                "\", got \"" + nextToken.getType() + ": " + nextToken.lexeme + "\"\n";
       System.err.print(errorMessage);
       errorWriter.write(errorMessage);
       errorWriter.flush();

       ArrayList<SyntaxSymbol> follow = followSet.get(parsingStack.peek());
       ArrayList<SyntaxSymbol> first = firstSet.get(parsingStack.peek());
       SyntaxSymbol lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
       SyntaxSymbol type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);

       if (nextToken.getType() == TokenType.END_OF_FILE || (follow.contains(lexeme) || follow.contains(type))) {
           parsingStack.pop();
       }
       else {
           while (nextToken.getType() != TokenType.END_OF_FILE &&
                   ((!first.contains(lexeme) && !first.contains(type)) ||
                           (first.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON))) &&
                                   !follow.contains(lexeme) &&
                                   !follow.contains(type))) {
               nextToken = analyser.nextToken();
               lexeme = new SyntaxSymbol(nextToken.lexeme, SyntaxSymbolType.TERMINAL);
               type = new SyntaxSymbol(nextToken.getType().toString(), SyntaxSymbolType.TERMINAL);
           }
       }
       return nextToken;
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
            followSet.put(terminal, new ArrayList<>(Arrays.asList(new SyntaxSymbol(";", SyntaxSymbolType.TERMINAL))));
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
                    else if (terminalName.contains("$")) {
                        arrayToAdd.add(new SyntaxSymbol("$", SyntaxSymbolType.END_OF_FILE));
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
            boolean allNull = true;
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
                    allNull = false;
                }
            }

            if (first.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON))) {
                ArrayList<SyntaxSymbol> follow = followSet.get(nonTerminal);
                for (SyntaxSymbol terminal:terminals) {
                    if (follow.contains(terminal)) {
                        row.put(terminal, production);
                        allNull = false;
                    }
                }
            }

            for (SyntaxSymbol terminal:terminals) {
                if (row.get(terminal) == null) {
                    row.put(terminal, null);
                }
            }
            if (allNull) {
               System.out.println(nonTerminal.name+" all null");
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
        }while ( (rhsSymbol instanceof SemanticSymbol ||
                firstSetRhsSymbol.contains(new SyntaxSymbol("EPSILON", SyntaxSymbolType.EPSILON))) &&
                i<production.rhs.size());
        return first;
    }

    private void outputParsingTableToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("out/test/a2/ParsingTable.tsv"));

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

    private void semanticProcessing(ASTNode currentNode) throws IOException {
        SymbolTableGenerationVisitor symbolTableCreation = new SymbolTableGenerationVisitor
                ("out/" + inputFile + ".outsemanticerrors");
        currentNode.accept(symbolTableCreation);

        SemanticCheckingVisitor semanticCheckingVisitor = new SemanticCheckingVisitor();
        currentNode.accept(semanticCheckingVisitor);
        Visitor.outputError();

        MemorySizeComputingVisitor memorySizeComputingVisitor = new MemorySizeComputingVisitor();
        currentNode.accept(memorySizeComputingVisitor);

        BufferedWriter symbolTableWriter = new BufferedWriter(new FileWriter("out/" + inputFile + "Table.csv"));
        symbolTableWriter.write("name, kind, type, size, link\n");
        symbolTableWriter.write(currentNode.table.toString());
        symbolTableWriter.close();

        CodeGenerationVisitor codeGenerationVisitor = new CodeGenerationVisitor("out/" + inputFile + ".moon");
        currentNode.accept(codeGenerationVisitor);
    }

}
