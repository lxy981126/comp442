import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    HashMap<NonTerminal, ArrayList<Symbol>> firstSet;
    HashMap<NonTerminal, ArrayList<Symbol>> followSet;
    Grammar grammar;

    public Parser() {
        try {
            this.grammar = new Grammar("./grm/non_ambiguous.grm");
            this.firstSet = buildFirstFollowSet("grm/non_ambiguous.grm.first");
            this.followSet = buildFirstFollowSet("grm/non_ambiguous.grm.follow");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashMap<NonTerminal, ArrayList<Symbol>> buildFirstFollowSet(String file)
            throws FileNotFoundException{
        HashMap<NonTerminal, ArrayList<Symbol>> set= new HashMap<>();
        Scanner scanner = new Scanner(new File(file));

        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains("FIRST") || line.contains("FOLLOW")) {
                int leftBracket = line.indexOf("<");
                int rightBracket = line.indexOf(">");
                String name = line.substring(leftBracket, rightBracket + 1);
                NonTerminal nonTerminal = new NonTerminal(name);

                ArrayList<Symbol> arrayToAdd = set.get(nonTerminal);
                if (arrayToAdd == null) {
                    arrayToAdd = new ArrayList<>();
                    set.put(nonTerminal, arrayToAdd);
                }

                int leftSquare = line.indexOf("[");
                int rightSquare = line.indexOf("]");
                String terminalsName = line.substring(leftSquare, rightSquare + 1);
                terminalsName = terminalsName.replaceAll("\\[", "").replaceAll("\\]","");
                String[] terminals = terminalsName.replaceAll(" ", "").split(",");

                for (String terminal:terminals) {
                    if (terminal.equals("EPSILON")) {
                        arrayToAdd.add(new Epsilon());
                    }
                    else {
                        arrayToAdd.add(new Terminal(terminal));
                    }
                }

            }
        }
        return set;
    }

}
