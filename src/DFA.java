public class DFA {

    public static State constructDFA(){
        State initial = new State(false, null);

        initial.addTransition('\n', initial);
        initial.addTransition('\t', initial);
        initial.addTransition('\r', initial);
        initial.addTransition(' ', initial);

        constructOperator(initial);
        constructString(initial);
        constructLetter(initial);
        constructComment(initial);

        return initial;
    }

    private static void constructOperator(State initial) {
        constructComparisonOperator(initial);
        constructArithmeticsOperator(initial);
        constructBinaryOperator(initial);
        constructParenthesis(initial);
        constructSeparator(initial);
    }

    private static void constructLetter(State initial) {
        constructA(initial);
        constructB(initial);
        constructC(initial);
        constructD(initial);
        constructE(initial);
        constructF(initial);
        constructI(initial);
    }

    private static void constructA(State initial) {
        State a = new State(true, TokenType.ID);
        initial.addTransition('a', a);
        addAlphaNumTransitions(a, a);
    }

    private static void constructB(State initial) {
        State b = new State(true, TokenType.ID);
        initial.addTransition('b', b);
        // break
        State br = new State(true, TokenType.ID);
        b.addTransition('r', br);
        addAlphaNumTransitionsWithException(b, b, new Character[]{'r'});

        State bre = new State(true, TokenType.ID);
        br.addTransition('e', bre);
        addAlphaNumTransitionsWithException(br, br, new Character[]{'e'});

        State brea = new State(true, TokenType.ID);
        bre.addTransition('a', brea);
        addAlphaNumTransitionsWithException(bre, bre, new Character[]{'a'});

        State breakState = new State(true, TokenType.BREAK);
        brea.addTransition('k', breakState);
        addAlphaNumTransitionsWithException(brea, brea, new Character[]{'k'});

        State breakId = new State(true, TokenType.ID);
        addAlphaNumTransitions(breakState, breakId);
        addAlphaNumTransitions(breakId, breakId);
    }

    private static void constructC(State initial) {
        State c = new State(true, TokenType.ID);
        initial.addTransition('c', c);

        State cl = new State(true, TokenType.ID);
        c.addTransition('l', cl);
        State co = new State(true, TokenType.ID);
        c.addTransition('o', co);
        addAlphaNumTransitionsWithException(c, c, new Character[]{'o', 'l'});

        // continue
        State con = new State(true, TokenType.ID);
        co.addTransition('n', con);
        addAlphaNumTransitionsWithException(co, co, new Character[]{'n'});

        State cont = new State(true, TokenType.ID);
        con.addTransition('t', cont);
        addAlphaNumTransitionsWithException(con, con, new Character[]{'t'});

        State conti = new State(true, TokenType.ID);
        cont.addTransition('i', conti);
        addAlphaNumTransitionsWithException(cont, cont, new Character[]{'i'});

        State contin = new State(true, TokenType.ID);
        conti.addTransition('n', contin);
        addAlphaNumTransitionsWithException(conti, conti, new Character[]{'n'});

        State continu = new State(true, TokenType.ID);
        contin.addTransition('u', continu);
        addAlphaNumTransitionsWithException(contin, contin, new Character[]{'u'});

        State continueState = new State(true, TokenType.CONTINUE);
        continu.addTransition('e', continueState);
        addAlphaNumTransitionsWithException(continu, continu, new Character[]{'e'});

        State continueId = new State(true, TokenType.ID);
        addAlphaNumTransitions(continueState, continueId);
        addAlphaNumTransitions(continueId, continueId);

        // class
        State cla = new State(true, TokenType.ID);
        cl.addTransition('a', cla);
        addAlphaNumTransitionsWithException(cl, cl, new Character[]{'a'});

        State clas = new State(true, TokenType.ID);
        cla.addTransition('s', clas);
        addAlphaNumTransitionsWithException(cla, cla, new Character[]{'s'});

        State classState = new State(true, TokenType.CLASS);
        clas.addTransition('s', classState);
        addAlphaNumTransitionsWithException(clas, clas, new Character[]{'s'});

        State classId = new State(true, TokenType.ID);
        addAlphaNumTransitions(classState, classId);
        addAlphaNumTransitions(classId, classId);
    }

    private static void constructD(State initial) {
        State d = new State(true, TokenType.ID);
        initial.addTransition('d', d);
        addAlphaNumTransitions(d, d);
    }

    private static void constructE(State initial) {
        State e = new State(true, TokenType.ID);
        initial.addTransition('e', e);

        State el = new State(true, TokenType.ID);
        e.addTransition('l', el);
        addAlphaNumTransitionsWithException(e, e, new Character[]{'l'});

        State els = new State(true, TokenType.ID);
        el.addTransition('s', els);
        addAlphaNumTransitionsWithException(el, el, new Character[]{'s'});

        State elseState = new State(true, TokenType.ELSE);
        els.addTransition('e', elseState);
        addAlphaNumTransitionsWithException(els, els, new Character[]{'e'});

        State elseId = new State(true, TokenType.ID);
        addAlphaNumTransitions(elseState, elseId);
        addAlphaNumTransitions(elseId, elseId);
    }

    private static void constructF(State initial) {
        State f = new State(true, TokenType.ID);
        initial.addTransition('f', f);

        State fl = new State(true, TokenType.ID);
        f.addTransition('l', fl);
        State fu = new State(true, TokenType.ID);
        f.addTransition('u', fu);
        addAlphaNumTransitionsWithException(f,f, new Character[]{'l','u'});

        // float
        State flo = new State(true, TokenType.ID);
        fl.addTransition('o', flo);
        addAlphaNumTransitionsWithException(fl,fl, new Character[]{'o'});

        State floa = new State(true, TokenType.ID);
        flo.addTransition('a', floa);
        addAlphaNumTransitionsWithException(flo,flo, new Character[]{'a'});

        State floatState = new State(true, TokenType.FLOAT);
        floa.addTransition('t', floatState);
        addAlphaNumTransitionsWithException(floa,floa, new Character[]{'t'});

        State floatId = new State(true, TokenType.ID);
        addAlphaNumTransitions(floatState,floatId);
        addAlphaNumTransitions(floatId,floatId);

        // func
        State fun = new State(true, TokenType.ID);
        fu.addTransition('n', fun);
        addAlphaNumTransitionsWithException(fu,fu, new Character[]{'n'});

        State func = new State(true, TokenType.FUNC);
        fun.addTransition('c', func);
        addAlphaNumTransitionsWithException(fun,fun, new Character[]{'c'});

        State funcId = new State(true, TokenType.ID);
        addAlphaNumTransitions(func,funcId);
        addAlphaNumTransitions(funcId,funcId);
    }

    private static void constructI(State initial) {
        State i = new State(true, TokenType.ID);
        initial.addTransition('i', i);

        // if
        State ifState = new State(true, TokenType.IF);
        i.addTransition('f', ifState);
        addAlphaNumTransitionsWithException(i, i, new Character[]{'f','n'});

        State ifId = new State(true, TokenType.ID);
        addAlphaNumTransitions(ifState, ifId);
        addAlphaNumTransitions(ifId, ifId);

        // integer
        State in = new State(true, TokenType.ID);
        i.addTransition('n', in);
        addAlphaNumTransitionsWithException(in, in, new Character[]{'t', 'h'});

        State intState = new State(true, TokenType.ID);
        in.addTransition('t', intState);
        addAlphaNumTransitionsWithException(intState, intState, new Character[]{'e'});

        State inte = new State(true, TokenType.ID);
        intState.addTransition('e', inte);
        addAlphaNumTransitionsWithException(inte, inte, new Character[]{'g'});

        State integ = new State(true, TokenType.ID);
        inte.addTransition('g', integ);
        addAlphaNumTransitionsWithException(integ, integ, new Character[]{'e'});

        State intege = new State(true, TokenType.ID);
        integ.addTransition('e', intege);
        addAlphaNumTransitionsWithException(intege, intege, new Character[]{'r'});

        State integer = new State(true, TokenType.INTEGER);
        intege.addTransition('r', integer);

        State integerId = new State(true, TokenType.ID);
        addAlphaNumTransitions(integer, integerId);
        addAlphaNumTransitions(integerId, integerId);

        // inherits
        State inh = new State(true, TokenType.ID);
        in.addTransition('h', inh);

        State inhe = new State(true, TokenType.ID);
        inh.addTransition('e', inhe);
        addAlphaNumTransitionsWithException(inh, inh, new Character[]{'e'});

        State inher = new State(true, TokenType.ID);
        inhe.addTransition('r', inher);
        addAlphaNumTransitionsWithException(inhe, inhe, new Character[]{'r'});

        State inheri = new State(true, TokenType.ID);
        inher.addTransition('i', inheri);
        addAlphaNumTransitionsWithException(inher, inher, new Character[]{'i'});

        State inherit = new State(true, TokenType.ID);
        inheri.addTransition('t', inherit);
        addAlphaNumTransitionsWithException(inheri, inheri, new Character[]{'t'});

        State inherits = new State(true, TokenType.INHERITS);
        inherit.addTransition('s', inherits);
        addAlphaNumTransitionsWithException(inherit, inherit, new Character[]{'s'});

        State inheritsId = new State(true, TokenType.ID);
        addAlphaNumTransitions(inherits, inheritsId);
        addAlphaNumTransitions(inheritsId, inheritsId);
    }

    private static void constructM(State initial) {

    }

    private static void constructComparisonOperator(State initial) {
        State assign = new State(true, TokenType.ASSIGN);
        initial.addTransition('=', assign);
        State equal = new State(true, TokenType.EQUAL);
        assign.addTransition('=', equal);

        State greater = new State(true, TokenType.GREATER_THAN);
        initial.addTransition('>', greater);
        State greaterEqual = new State(true, TokenType.GREATER_EQUAL);
        greater.addTransition('=', greaterEqual);

        State less = new State(true, TokenType.LESS_THAN);
        initial.addTransition('<', less);
        State lessEqual = new State(true, TokenType.LESS_EQUAL);
        less.addTransition('=', lessEqual);
        State notEqual = new State(true, TokenType.NOT_EQUAL);
        less. addTransition('>', notEqual);
    }

    private static void constructArithmeticsOperator(State initial) {
        State plus = new State(true,TokenType.PLUS);
        initial.addTransition('+',plus);

        State minus = new State(true, TokenType.MINUS);
        initial.addTransition('-',minus);

        State multiplication = new State(true, TokenType.MULTIPLICATION);
        initial.addTransition('*',multiplication);
    }

    private static void constructBinaryOperator(State initial) {
        State or = new State(true, TokenType.OR);
        initial.addTransition('|', or);

        State and = new State(true, TokenType.AND);
        initial.addTransition('&',and);

        State not = new State(true,TokenType.NOT);
        initial.addTransition('!',not);

        State qmark = new State(true, TokenType.QUESTION_MARK);
        initial.addTransition('?',qmark);
    }

    private static void constructParenthesis(State initial) {
        State openpar = new State(true, TokenType.OPEN_PARENTHESIS);
        initial.addTransition('(',openpar);

        State closepar = new State(true, TokenType.CLOSE_PARENTHESIS);
        initial.addTransition(')',closepar);

        State opensqbr = new State(true, TokenType.OPEN_SQUARE_BRACKET);
        initial.addTransition('[',opensqbr);

        State closesqbr = new State(true, TokenType.CLOSE_SQUARE_BRACKET);
        initial.addTransition(']',closesqbr);

        State opencubr = new State(true, TokenType.OPEN_CURLY_BRACKET);
        initial.addTransition('{',opencubr);

        State closecubr = new State(true, TokenType.CLOSE_CURLY_BRACKET);
        initial.addTransition('}',closecubr);
    }

    private static void constructSeparator(State initial) {
        State semi = new State(true, TokenType.SEMICOLON);
        initial.addTransition(';', semi);
        State comma = new State(true, TokenType.COMMA);
        initial.addTransition(',',comma);

        State colon = new State(true, TokenType.COLON);
        initial.addTransition(':', colon);
        State colonColon = new State (true, TokenType.COLON_COLON);
        colon.addTransition(':', colonColon);
    }

    private static void constructString(State initial) {
        State stringStart = new State(false, TokenType.STRING);
        initial.addTransition('"', stringStart);

        addAlphaNumTransitions(stringStart, stringStart);
        stringStart.addTransition(' ', stringStart);

        State stringFinal = new State(true, TokenType.STRING);
        stringStart.addTransition('"',stringFinal);
    }

    private static void constructComment(State initial) {
        State div = new State(true, TokenType.DIVISION);
        initial.addTransition('/',div);

        // inline comment
        State inline = new State(true, TokenType.INLINE_COMMENT);
        div.addTransition('/', inline);
        inline.addTransitions(' ', (char)255, inline);

        // block comment
        State blockStart = new State(false, TokenType.BLOCK_COMMENT);
        div.addTransition('*', blockStart);
        blockStart.addTransitionsWithException((char)0, (char)255, blockStart, new Character[]{'*'});

        State blockEnd = new State(false, TokenType.BLOCK_COMMENT);
        blockStart.addTransition('*', blockEnd);

        State blockEndFinal = new State(true, TokenType.BLOCK_COMMENT);
        blockEnd.addTransition('/', blockEndFinal);
    }

    private static void addAlphaNumTransitions(State from, State to) {
        from.addTransitions('0','9', to);
        from.addTransitions('A','Z', to);
        from.addTransitions('a','z', to);
        from.addTransition('_', to);
    }

    private static void addAlphaNumTransitionsWithException(State from, State to, Character[] exception) {
        from.addTransitionsWithException('0','9', to, exception);
        from.addTransitionsWithException('A','Z', to, exception);
        from.addTransitionsWithException('a','z', to, exception);
        from.addTransition('_', to);
    }

}
