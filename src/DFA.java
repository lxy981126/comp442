public class DFA {

    public static State constructDFA(){
        State initial = new State(false, null);
        initial.addTransition('\n', initial);
        initial.addTransition('\t', initial);
        initial.addTransition('\r', initial);
        initial.addTransition(' ', initial);

        State semi = new State(true, TokenType.semi);
        initial.addTransition(';', semi);
        State or = new State(true, TokenType.or);
        initial.addTransition('|', or);
        State minus = new State(true, TokenType.minus);
        initial.addTransition('-',minus);
        State comma = new State(true, TokenType.comma);
        initial.addTransition(',',comma);
        State and = new State(true, TokenType.and);
        initial.addTransition('&',and);
        State plus = new State(true,TokenType.plus);
        initial.addTransition('+',plus);
        State not = new State(true,TokenType.not);
        initial.addTransition('!',not);
        State openpar = new State(true, TokenType.openpar);
        initial.addTransition('(',openpar);
        State closepar = new State(true, TokenType.closepar);
        initial.addTransition(')',closepar);
        State opensqbr = new State(true, TokenType.opensqbr);
        initial.addTransition('[',opensqbr);
        State closesqbr = new State(true, TokenType.closesqbr);
        initial.addTransition(']',closesqbr);
        State opencubr = new State(true, TokenType.opencubr);
        initial.addTransition('{',opencubr);
        State closecubr = new State(true, TokenType.closecubr);
        initial.addTransition('}',closecubr);
        State qmark = new State(true, TokenType.qmark);
        initial.addTransition('?',qmark);
        State mult = new State(true, TokenType.mult);
        initial.addTransition('*',mult);

        State assign = new State(true, TokenType.assign);
        initial.addTransition('=', assign);
        State equal = new State(true, TokenType.eq);
        assign.addTransition('=', equal);

        State greater = new State(true, TokenType.gt);
        initial.addTransition('>', greater);
        State greaterEqual = new State(true, TokenType.geq);
        greater.addTransition('=', greaterEqual);

        State less = new State(true, TokenType.lt);
        initial.addTransition('<', less);
        State lessEqual = new State(true, TokenType.leq);
        less.addTransition('=', lessEqual);
        State notEqual = new State(true, TokenType.noteq);
        less. addTransition('>', notEqual);

        State colon = new State(true, TokenType.colon);
        initial.addTransition(':', colon);
        State colonColon = new State (true, TokenType.coloncolon);
        colon.addTransition(':', colonColon);

        State div = new State(true, TokenType.div);
        initial.addTransition('/',div);
        // inline comment
        State inline = new State(true, TokenType.inlinecmt);
        div.addTransition('/', inline);
        inline.addTransitions(' ', (char)255, inline);
        // block comment
        State blockStart = new State(false, TokenType.blockcmt);
        div.addTransition('*', blockStart);
        blockStart.addTransitionsWithException((char)0, (char)255, blockStart, '*');
        State blockEnd = new State(false, TokenType.blockcmt);
        blockStart.addTransition('*', blockEnd);
        State blockEndFinal = new State(true, TokenType.blockcmt);
        blockEnd.addTransition('/', blockEndFinal);


        


        return initial;
    }
}
