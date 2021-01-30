public class DFA {

    public static State constructDFA(){
        State initial = new State(false, null);
        initial.addChild('\n', initial);
        initial.addChild('\t', initial);
        initial.addChild('\r', initial);
        initial.addChild(' ', initial);
        State q3 = new State(true, TokenType.quote);
        initial.addChild('"', q3);
        State q13 = new State(true, TokenType.semi);
        initial.addChild(';', q13);
        State q18 = new State(true, TokenType.or);
        initial.addChild('|', q18);
        State q19 = new State(true, TokenType.minus);
        initial.addChild('-',q19);
        State q31 = new State(true, TokenType.comma);
        initial.addChild(',',q31);
        State q28 = new State(true, TokenType.and);
        initial.addChild('&',q28);
        State q27 = new State(true, TokenType.plus);
        initial.addChild('+',q27);
        State q11 = new State(true, TokenType.not);
        initial.addChild('!',q11);
        State q35 = new State(true, TokenType.openpar);
        initial.addChild('(',q35);
        State q36 = new State(true, TokenType.closepar);
        initial.addChild(')',q36);
        State q30 = new State(true, TokenType.opensqbr);
        initial.addChild('[',q30);
        State q8 = new State(true, TokenType.closesqbr);
        initial.addChild(']',q8);
        State q37 = new State(true, TokenType.opencubr);
        initial.addChild('{',q37);
        State q2 = new State(true, TokenType.closecubr);
        initial.addChild('}',q2);
        State q32 = new State(true, TokenType.qmark);
        initial.addChild('?',q32);
        State q12 = new State(true, TokenType.mult);
        initial.addChild('*',q12);
        State q29 = new State(true, TokenType.div);
        initial.addChild('/',q29);

        return initial;
    }

}
