public class DFA {

    public static State constructDFA(){
        State initial = new State(null);
        State invalid = new State(TokenType.INVALID_CHARACTER);

        addDelimiterTransitions(initial, initial);
        addInvalidCharacters(initial, invalid);

        constructOperator(initial);
        constructNumber(initial);
        constructId(initial);
        constructString(initial);
        constructComment(initial);

        return initial;
    }

    private static void constructOperator(State initial) {
        constructComparisonOperator(initial);
        constructArithmeticsOperator(initial);
        constructBinaryOperator(initial);
        constructParenthesis(initial);
        constructPunctuation(initial);
    }

    private static void constructNumber(State initial) {
        State zero = new State(TokenType.INTEGER_NUMBER);
        initial.addTransition('0', zero);

        State nonZero = new State(TokenType.INTEGER_NUMBER);
        initial.addTransitions('1', '9', nonZero);

        State intDigit = new State(TokenType.INTEGER_NUMBER);
        nonZero.addTransitions('0', '9', intDigit);
        intDigit.addTransitions('0', '9', intDigit);

        State dot = new State(TokenType.INVALID_NUMBER);
        zero.addTransition('.', dot);
        nonZero.addTransition('.', dot);
        intDigit.addTransition('.', dot);

        State dotNonzero = new State(TokenType.FLOAT_NUMBER);
        dot.addTransitions('1', '9', dotNonzero);
        dotNonzero.addTransitions('1','9', dotNonzero);

        State dotZeroFinal = new State(TokenType.FLOAT_NUMBER);
        dot.addTransition('0', dotZeroFinal);

        State dotZeroZero = new State(TokenType.INVALID_NUMBER);
        dotZeroFinal.addTransition('0', dotZeroZero);
        dotNonzero.addTransition('0', dotZeroZero);
        dotZeroZero.addTransition('0', dotZeroZero);

        State fraction = new State(TokenType.FLOAT_NUMBER);
        dotZeroFinal.addTransitions('1', '9', fraction);
        dotZeroZero.addTransitions('1','9', fraction);
        fraction.addTransitions('1','9', fraction);
        fraction.addTransition('0', dotZeroZero);

        State e = new State(TokenType.INVALID_NUMBER);
        dotZeroFinal.addTransition('e', e);
        dotNonzero.addTransition('e', e);
        fraction.addTransition('e', e);

        State eZero = new State(TokenType.FLOAT_NUMBER);
        e.addTransition('0', eZero);

        State ePlusMinus = new State(TokenType.INVALID_NUMBER);
        e.addTransition('+', ePlusMinus);
        e.addTransition('-', ePlusMinus);
        ePlusMinus.addTransition('0', eZero);

        State eNonzero = new State(TokenType.FLOAT_NUMBER);
        e.addTransitions('1','9', eNonzero);
        ePlusMinus.addTransitions('1','9', eNonzero);
        eNonzero.addTransitions('0', '9', eNonzero);

    }

    private static void constructId(State initial) {
        constructB(initial);
        constructC(initial);
        constructD(initial);
        constructE(initial);
        constructF(initial);
        constructI(initial);
        constructM(initial);
        constructP(initial);
        constructR(initial);
        constructS(initial);
        constructT(initial);
        constructV(initial);
        constructW(initial);

        Character[] excludedChar =  new Character[]{'b','c','d','e','f','i','m','p','r','s','t','v','w'};
        State id = new State(TokenType.ID);
        addLetterTransitionsWithException(initial, id, excludedChar);
        addAlphaNumTransitions(id, id);
    }

    private static void constructB(State initial) {
        State b = new State(TokenType.ID);
        initial.addTransition('b', b);
        // break
        State br = new State(TokenType.ID);
        b.addTransition('r', br);
        addAlphaNumTransitionsWithException(b, b, new Character[]{'r'});

        State bre = new State(TokenType.ID);
        br.addTransition('e', bre);
        addAlphaNumTransitionsWithException(br, br, new Character[]{'e'});

        State brea = new State(TokenType.ID);
        bre.addTransition('a', brea);
        addAlphaNumTransitionsWithException(bre, bre, new Character[]{'a'});

        State breakState = new State(TokenType.BREAK);
        brea.addTransition('k', breakState);
        addAlphaNumTransitionsWithException(brea, brea, new Character[]{'k'});

        State breakId = new State(TokenType.ID);
        addAlphaNumTransitions(breakState, breakId);
        addAlphaNumTransitions(breakId, breakId);
    }

    private static void constructC(State initial) {
        State c = new State(TokenType.ID);
        initial.addTransition('c', c);

        State cl = new State(TokenType.ID);
        c.addTransition('l', cl);
        State co = new State(TokenType.ID);
        c.addTransition('o', co);
        addAlphaNumTransitionsWithException(c, c, new Character[]{'o', 'l'});

        // continue
        State con = new State(TokenType.ID);
        co.addTransition('n', con);
        addAlphaNumTransitionsWithException(co, co, new Character[]{'n'});

        State cont = new State(TokenType.ID);
        con.addTransition('t', cont);
        addAlphaNumTransitionsWithException(con, con, new Character[]{'t'});

        State conti = new State(TokenType.ID);
        cont.addTransition('i', conti);
        addAlphaNumTransitionsWithException(cont, cont, new Character[]{'i'});

        State contin = new State(TokenType.ID);
        conti.addTransition('n', contin);
        addAlphaNumTransitionsWithException(conti, conti, new Character[]{'n'});

        State continu = new State(TokenType.ID);
        contin.addTransition('u', continu);
        addAlphaNumTransitionsWithException(contin, contin, new Character[]{'u'});

        State continueState = new State(TokenType.CONTINUE);
        continu.addTransition('e', continueState);
        addAlphaNumTransitionsWithException(continu, continu, new Character[]{'e'});

        State continueId = new State(TokenType.ID);
        addAlphaNumTransitions(continueState, continueId);
        addAlphaNumTransitions(continueId, continueId);

        // class
        State cla = new State(TokenType.ID);
        cl.addTransition('a', cla);
        addAlphaNumTransitionsWithException(cl, cl, new Character[]{'a'});

        State clas = new State(TokenType.ID);
        cla.addTransition('s', clas);
        addAlphaNumTransitionsWithException(cla, cla, new Character[]{'s'});

        State classState = new State(TokenType.CLASS);
        clas.addTransition('s', classState);
        addAlphaNumTransitionsWithException(clas, clas, new Character[]{'s'});

        State classId = new State(TokenType.ID);
        addAlphaNumTransitions(classState, classId);
        addAlphaNumTransitions(classId, classId);
    }

    private static void constructD(State initial) {
        State d = new State(TokenType.ID);
        initial.addTransition('d', d);
        addAlphaNumTransitions(d, d);
    }

    private static void constructE(State initial) {
        State e = new State(TokenType.ID);
        initial.addTransition('e', e);

        State el = new State(TokenType.ID);
        e.addTransition('l', el);
        addAlphaNumTransitionsWithException(e, e, new Character[]{'l'});

        State els = new State(TokenType.ID);
        el.addTransition('s', els);
        addAlphaNumTransitionsWithException(el, el, new Character[]{'s'});

        State elseState = new State(TokenType.ELSE);
        els.addTransition('e', elseState);
        addAlphaNumTransitionsWithException(els, els, new Character[]{'e'});

        State elseId = new State(TokenType.ID);
        addAlphaNumTransitions(elseState, elseId);
        addAlphaNumTransitions(elseId, elseId);
    }

    private static void constructF(State initial) {
        State f = new State(TokenType.ID);
        initial.addTransition('f', f);

        State fl = new State(TokenType.ID);
        f.addTransition('l', fl);
        State fu = new State(TokenType.ID);
        f.addTransition('u', fu);
        addAlphaNumTransitionsWithException(f,f, new Character[]{'l','u'});

        // float
        State flo = new State(TokenType.ID);
        fl.addTransition('o', flo);
        addAlphaNumTransitionsWithException(fl,fl, new Character[]{'o'});

        State floa = new State(TokenType.ID);
        flo.addTransition('a', floa);
        addAlphaNumTransitionsWithException(flo,flo, new Character[]{'a'});

        State floatState = new State(TokenType.FLOAT);
        floa.addTransition('t', floatState);
        addAlphaNumTransitionsWithException(floa,floa, new Character[]{'t'});

        State floatId = new State(TokenType.ID);
        addAlphaNumTransitions(floatState,floatId);
        addAlphaNumTransitions(floatId,floatId);

        // func
        State fun = new State(TokenType.ID);
        fu.addTransition('n', fun);
        addAlphaNumTransitionsWithException(fu,fu, new Character[]{'n'});

        State func = new State(TokenType.FUNC);
        fun.addTransition('c', func);
        addAlphaNumTransitionsWithException(fun,fun, new Character[]{'c'});

        State funcId = new State(TokenType.ID);
        addAlphaNumTransitions(func,funcId);
        addAlphaNumTransitions(funcId,funcId);
    }

    private static void constructI(State initial) {
        State i = new State(TokenType.ID);
        initial.addTransition('i', i);

        // if
        State ifState = new State(TokenType.IF);
        i.addTransition('f', ifState);
        addAlphaNumTransitionsWithException(i, i, new Character[]{'f','n'});

        State ifId = new State(TokenType.ID);
        addAlphaNumTransitions(ifState, ifId);
        addAlphaNumTransitions(ifId, ifId);

        // integer
        State in = new State(TokenType.ID);
        i.addTransition('n', in);
        addAlphaNumTransitionsWithException(in, in, new Character[]{'t', 'h'});

        State intState = new State(TokenType.ID);
        in.addTransition('t', intState);
        addAlphaNumTransitionsWithException(intState, intState, new Character[]{'e'});

        State inte = new State(TokenType.ID);
        intState.addTransition('e', inte);
        addAlphaNumTransitionsWithException(inte, inte, new Character[]{'g'});

        State integ = new State(TokenType.ID);
        inte.addTransition('g', integ);
        addAlphaNumTransitionsWithException(integ, integ, new Character[]{'e'});

        State intege = new State(TokenType.ID);
        integ.addTransition('e', intege);
        addAlphaNumTransitionsWithException(intege, intege, new Character[]{'r'});

        State integer = new State(TokenType.INTEGER);
        intege.addTransition('r', integer);

        State integerId = new State(TokenType.ID);
        addAlphaNumTransitions(integer, integerId);
        addAlphaNumTransitions(integerId, integerId);

        // inherits
        State inh = new State(TokenType.ID);
        in.addTransition('h', inh);

        State inhe = new State(TokenType.ID);
        inh.addTransition('e', inhe);
        addAlphaNumTransitionsWithException(inh, inh, new Character[]{'e'});

        State inher = new State(TokenType.ID);
        inhe.addTransition('r', inher);
        addAlphaNumTransitionsWithException(inhe, inhe, new Character[]{'r'});

        State inheri = new State(TokenType.ID);
        inher.addTransition('i', inheri);
        addAlphaNumTransitionsWithException(inher, inher, new Character[]{'i'});

        State inherit = new State(TokenType.ID);
        inheri.addTransition('t', inherit);
        addAlphaNumTransitionsWithException(inheri, inheri, new Character[]{'t'});

        State inherits = new State(TokenType.INHERITS);
        inherit.addTransition('s', inherits);
        addAlphaNumTransitionsWithException(inherit, inherit, new Character[]{'s'});

        State inheritsId = new State(TokenType.ID);
        addAlphaNumTransitions(inherits, inheritsId);
        addAlphaNumTransitions(inheritsId, inheritsId);
    }

    private static void constructM(State initial) {
        State m = new State(TokenType.ID);
        initial.addTransition('m', m);

        State ma = new State(TokenType.ID);
        m.addTransition('a', ma);
        addAlphaNumTransitionsWithException(m, m, new Character[]{'a'});

        State mai = new State(TokenType.ID);
        ma.addTransition('i', mai);
        addAlphaNumTransitionsWithException(ma, ma, new Character[]{'i'});

        State main = new State(TokenType.MAIN);
        mai.addTransition('n', main);

        State mainId = new State(TokenType.ID);
        addAlphaNumTransitions(main, mainId);
        addAlphaNumTransitions(mainId, mainId);
    }

    private static void constructP(State initial) {
        State p = new State(TokenType.ID);
        initial.addTransition('p', p);
        addAlphaNumTransitionsWithException(p,p,new Character[]{'r', 'u'});

        // private
        State pr = new State(TokenType.ID);
        p.addTransition('r', pr);

        State pri = new State(TokenType.ID);
        pr.addTransition('i', pri);
        addAlphaNumTransitionsWithException(pr, pr, new Character[]{'i'});

        State priv = new State(TokenType.ID);
        pri.addTransition('v', priv);
        addAlphaNumTransitionsWithException(pri,pri, new Character[]{'v'});

        State priva = new State(TokenType.ID);
        priv.addTransition('a', priva);
        addAlphaNumTransitionsWithException(priv, priv, new Character[]{'a'});

        State privat = new State(TokenType.ID);
        priva.addTransition('t', privat);
        addAlphaNumTransitionsWithException(priva,priva, new Character[]{'t'});

        State privateState = new State(TokenType.PRIVATE);
        privat.addTransition('e',privateState);
        addAlphaNumTransitionsWithException(privat, privat, new Character[]{'e'});

        State privateId = new State(TokenType.ID);
        addAlphaNumTransitions(privateState, privateId);
        addAlphaNumTransitions(privateId, privateId);

        // public
        State pu = new State(TokenType.ID);
        p.addTransition('u', pu);

        State pub = new State(TokenType.ID);
        pu.addTransition('b', pub);
        addAlphaNumTransitionsWithException(pu, pu, new Character[]{'b'});

        State publ = new State(TokenType.ID);
        pub.addTransition('l', publ);
        addAlphaNumTransitionsWithException(pub, pub, new Character[]{'l'});

        State publi = new State(TokenType.ID);
        publ.addTransition('i', publi);
        addAlphaNumTransitionsWithException(publ, publ, new Character[]{'i'});

        State publicState = new State(TokenType.PUBLIC);
        publi.addTransition('c',publicState);
        addAlphaNumTransitionsWithException(publi, publi, new Character[]{'c'});

        State publicId = new State(TokenType.ID);
        addAlphaNumTransitions(publicState, publicId);
        addAlphaNumTransitions(publicId, publicId);
    }

    private static void constructR(State initial){
        State r = new State(TokenType.ID);
        initial.addTransition('r', r);

        State re = new State(TokenType.ID);
        r.addTransition('e', re);
        addAlphaNumTransitionsWithException(r,r, new Character[]{'e'});

        // read
        State rea = new State(TokenType.ID);
        re.addTransition('a', rea);
        addAlphaNumTransitionsWithException(re, re, new Character[]{'a', 't'});

        State read = new State(TokenType.READ);
        rea.addTransition('d', read);
        addAlphaNumTransitionsWithException(rea, rea, new Character[]{'d'});

        State readId = new State(TokenType.ID);
        addAlphaNumTransitions(read, readId);
        addAlphaNumTransitions(readId, readId);

        // return
        State ret = new State(TokenType.ID);
        re.addTransition('t', ret);

        State retu = new State(TokenType.ID);
        ret.addTransition('u', retu);
        addAlphaNumTransitionsWithException(ret, ret, new Character[]{'u'});

        State retur = new State(TokenType.ID);
        retu.addTransition('r', retur);
        addAlphaNumTransitionsWithException(retu, retu, new Character[]{'r'});

        State returnState = new State(TokenType.RETURN);
        retur.addTransition('n', returnState);
        addAlphaNumTransitionsWithException(retur, retur, new Character[]{'n'});

        State returnId = new State(TokenType.ID);
        addAlphaNumTransitions(returnState, returnId);
        addAlphaNumTransitions(returnId, returnId);
    }

    private static void constructS(State initial) {
        State s = new State(TokenType.ID);
        initial.addTransition('s', s);

        // string
        State st = new State(TokenType.ID);
        s.addTransition('t', st);
        addAlphaNumTransitionsWithException(s, s, new Character[]{'t'});

        State str = new State(TokenType.ID);
        st.addTransition('r', str);
        addAlphaNumTransitionsWithException(st, st, new Character[]{'r'});

        State stri = new State(TokenType.ID);
        str.addTransition('i', stri);
        addAlphaNumTransitionsWithException(str, str, new Character[]{'i'});

        State strin = new State(TokenType.ID);
        stri.addTransition('n', strin);
        addAlphaNumTransitionsWithException(stri, stri, new Character[]{'n'});

        State string = new State(TokenType.STRING);
        strin.addTransition('g', string);
        addAlphaNumTransitionsWithException(strin, strin, new Character[]{'g'});

        State stringId = new State(TokenType.ID);
        addAlphaNumTransitions(string, stringId);
        addAlphaNumTransitions(stringId, stringId);
    }

    private static void constructT(State initial) {
        State t = new State(TokenType.ID);
        initial.addTransition('t', t);

        // then
        State th = new State(TokenType.ID);
        t.addTransition('h', th);
        addAlphaNumTransitionsWithException(t, t, new Character[]{'h'});

        State the = new State(TokenType.ID);
        th.addTransition('e', the);
        addAlphaNumTransitionsWithException(th, th, new Character[]{'e'});

        State then = new State(TokenType.THEN);
        the.addTransition('n', then);
        addAlphaNumTransitionsWithException(the, the, new Character[]{'n'});

        State thenId = new State(TokenType.ID);
        addAlphaNumTransitions(then, thenId);
        addAlphaNumTransitions(thenId, thenId);
    }

    private static void constructV(State initial) {
        State v = new State(TokenType.ID);
        initial.addTransition('v', v);
        addAlphaNumTransitionsWithException(v, v, new Character[]{'a', 'o'});

        // var
        State va = new State(TokenType.ID);
        v.addTransition('a', va);

        State var = new State(TokenType.VAR);
        va.addTransition('r', var);
        addAlphaNumTransitionsWithException(va, va, new Character[]{'r'});

        State varId = new State(TokenType.ID);
        addAlphaNumTransitions(var, varId);
        addAlphaNumTransitions(varId, varId);

        // void
        State vo = new State(TokenType.ID);
        v.addTransition('o', vo);

        State voi = new State(TokenType.ID);
        vo.addTransition('i', voi);
        addAlphaNumTransitionsWithException(vo, vo, new Character[]{'i'});

        State voidState = new State(TokenType.VOID);
        voi.addTransition('d', voidState);
        addAlphaNumTransitionsWithException(voi, voi, new Character[]{'d'});

        State voidId = new State(TokenType.ID);
        addAlphaNumTransitions(voidState, voidId);
        addAlphaNumTransitions(voidId, voidId);
    }

    private static void constructW(State initial) {
        State w = new State(TokenType.ID);
        initial.addTransition('w', w);
        addAlphaNumTransitionsWithException(w, w, new Character[]{'h', 'r'});

        // while
        State wh = new State(TokenType.ID);
        w.addTransition('h', wh);

        State whi = new State(TokenType.ID);
        wh.addTransition('i', whi);
        addAlphaNumTransitionsWithException(wh, wh, new Character[]{'i'});

        State whil = new State(TokenType.ID);
        whi.addTransition('l', whil);
        addAlphaNumTransitionsWithException(whi, whi, new Character[]{'l'});

        State whileState = new State(TokenType.WHILE);
        whil.addTransition('e', whileState);
        addAlphaNumTransitionsWithException(whil, whil, new Character[]{'e'});

        State whileId = new State(TokenType.ID);
        addAlphaNumTransitions(whileState, whileId);
        addAlphaNumTransitions(whileId, whileId);

        // write
        State wr = new State(TokenType.ID);
        w.addTransition('r', wr);

        State wri = new State(TokenType.ID);
        wr.addTransition('i', wri);
        addAlphaNumTransitionsWithException(wr, wr, new Character[]{'i'});

        State writ = new State(TokenType.ID);
        wri.addTransition('t', writ);
        addAlphaNumTransitionsWithException(wri, wri, new Character[]{'t'});

        State write = new State(TokenType.WRITE);
        writ.addTransition('e', write);
        addAlphaNumTransitionsWithException(writ, writ, new Character[]{'e'});

        State writeId = new State(TokenType.ID);
        addAlphaNumTransitions(write, writeId);
        addAlphaNumTransitions(writeId, writeId);
    }

    private static void constructComparisonOperator(State initial) {
        State assign = new State(TokenType.ASSIGN);
        initial.addTransition('=', assign);
        State equal = new State(TokenType.EQUAL);
        assign.addTransition('=', equal);

        State greater = new State(TokenType.GREATER_THAN);
        initial.addTransition('>', greater);
        State greaterEqual = new State(TokenType.GREATER_EQUAL);
        greater.addTransition('=', greaterEqual);

        State less = new State(TokenType.LESS_THAN);
        initial.addTransition('<', less);
        State lessEqual = new State(TokenType.LESS_EQUAL);
        less.addTransition('=', lessEqual);
        State notEqual = new State(TokenType.NOT_EQUAL);
        less. addTransition('>', notEqual);
    }

    private static void constructArithmeticsOperator(State initial) {
        State plus = new State(TokenType.PLUS);
        initial.addTransition('+',plus);

        State minus = new State(TokenType.MINUS);
        initial.addTransition('-',minus);

        State multiplication = new State(TokenType.MULTIPLICATION);
        initial.addTransition('*',multiplication);
    }

    private static void constructBinaryOperator(State initial) {
        State or = new State(TokenType.OR);
        initial.addTransition('|', or);

        State and = new State(TokenType.AND);
        initial.addTransition('&',and);

        State not = new State(TokenType.NOT);
        initial.addTransition('!',not);

        State qmark = new State(TokenType.QUESTION_MARK);
        initial.addTransition('?',qmark);
    }

    private static void constructParenthesis(State initial) {
        State openpar = new State(TokenType.OPEN_PARENTHESIS);
        initial.addTransition('(',openpar);

        State closepar = new State(TokenType.CLOSE_PARENTHESIS);
        initial.addTransition(')',closepar);

        State opensqbr = new State(TokenType.OPEN_SQUARE_BRACKET);
        initial.addTransition('[',opensqbr);

        State closesqbr = new State(TokenType.CLOSE_SQUARE_BRACKET);
        initial.addTransition(']',closesqbr);

        State opencubr = new State(TokenType.OPEN_CURLY_BRACKET);
        initial.addTransition('{',opencubr);

        State closecubr = new State(TokenType.CLOSE_CURLY_BRACKET);
        initial.addTransition('}',closecubr);
    }

    private static void constructPunctuation(State initial) {
        State dot = new State(TokenType.DOT);
        initial.addTransition('.',dot);
        State semi = new State(TokenType.SEMICOLON);
        initial.addTransition(';', semi);
        State comma = new State(TokenType.COMMA);
        initial.addTransition(',',comma);

        State colon = new State(TokenType.COLON);
        initial.addTransition(':', colon);
        State colonColon = new State (TokenType.COLON_COLON);
        colon.addTransition(':', colonColon);
    }

    private static void constructString(State initial) {
        State stringStart = new State(TokenType.INVALID_STRING);
        initial.addTransition('"', stringStart);

        addAlphaNumTransitions(stringStart, stringStart);
        stringStart.addTransition(' ', stringStart);

        State stringFinal = new State(TokenType.STRING_LITERAL);
        stringStart.addTransition('"',stringFinal);
    }

    private static void constructComment(State initial) {
        State div = new State(TokenType.DIVISION);
        initial.addTransition('/',div);

        // inline comment
        State inline = new State(TokenType.INLINE_COMMENT);
        div.addTransition('/', inline);
        inline.addTransitions(' ', (char)255, inline);

        // block comment
        State blockStart = new State(TokenType.INVALID_COMMENT);
        div.addTransition('*', blockStart);
        blockStart.addTransitionsWithException((char)0, (char)255, blockStart, new Character[]{'*'});

        State blockEnd = new State(TokenType.INVALID_COMMENT);
        blockStart.addTransition('*', blockEnd);

        State blockEndFinal = new State(TokenType.BLOCK_COMMENT);
        blockEnd.addTransition('/', blockEndFinal);
    }

    private static void addLetterTransitions(State from, State to) {
        from.addTransitions('A','Z', to);
        from.addTransitions('a','z', to);
    }

    private static void addLetterTransitionsWithException(State from, State to, Character[] exceptions) {
        from.addTransitionsWithException('A','Z', to, exceptions);
        from.addTransitionsWithException('a','z', to, exceptions);
    }

    private static void addAlphaNumTransitions(State from, State to) {
        addLetterTransitions(from, to);
        from.addTransitions('0','9', to);
        from.addTransition('_', to);
    }

    private static void addAlphaNumTransitionsWithException(State from, State to, Character[] exception) {
        addLetterTransitionsWithException(from, to, exception);
        from.addTransitionsWithException('0','9', to, exception);
        from.addTransition('_', to);
    }

    private static void addDelimiterTransitions(State from, State to) {
        from.addTransition(' ', to);
        from.addTransition('\n', to);
        from.addTransition('\t', to);
        from.addTransition('\r', to);
    }

    private static void addInvalidCharacters(State from, State to) {
        from.addTransition('#', to);
        from.addTransition('$', to);
        from.addTransition('%', to);
        from.addTransition('\'', to);
        from.addTransition('@', to);
        from.addTransition('\\', to);
        from.addTransition('`', to);
        from.addTransitions('~','ÿ', to);
    }
}
