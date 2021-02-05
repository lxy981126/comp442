public class DFA {

    public static State constructDFA(){
        State initial = new State(false, null);

        initial.addTransition('\n', initial);
        initial.addTransition('\t', initial);
        initial.addTransition('\r', initial);
        initial.addTransition(' ', initial);

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
        State zero = new State(true, TokenType.INTEGER_NUMBER);
        initial.addTransition('0', zero);

        State nonZero = new State(true, TokenType.INTEGER_NUMBER);
        initial.addTransitions('1', '9', nonZero);

        State intDigit = new State(true, TokenType.INTEGER_NUMBER);
        nonZero.addTransitions('0', '9', intDigit);
        intDigit.addTransitions('0', '9', intDigit);

        State dot = new State(false, TokenType.FLOAT_NUMBER);
        zero.addTransition('.', dot);
        nonZero.addTransition('.', dot);
        intDigit.addTransition('.', dot);

        State dotNonzero = new State(true, TokenType.FLOAT_NUMBER);
        dot.addTransitions('1', '9', dotNonzero);
        dotNonzero.addTransitions('1','9', dotNonzero);

        State dotZeroFinal = new State(true, TokenType.FLOAT_NUMBER);
        dot.addTransition('0', dotZeroFinal);

        State dotZeroZero = new State(false, TokenType.FLOAT_NUMBER);
        dotZeroFinal.addTransition('0', dotZeroZero);
        dotNonzero.addTransition('0', dotZeroZero);
        dotZeroZero.addTransition('0', dotZeroZero);

        State fraction = new State(true, TokenType.FLOAT_NUMBER);
        dotZeroFinal.addTransitions('1', '9', fraction);
        dotZeroZero.addTransitions('1','9', fraction);
        fraction.addTransitions('1','9', fraction);
        fraction.addTransition('0', dotZeroZero);

        State e = new State(false, TokenType.FLOAT_NUMBER);
        dotZeroFinal.addTransition('e', e);
        dotNonzero.addTransition('e', e);
        fraction.addTransition('e', e);

        State eZero = new State(true, TokenType.FLOAT_NUMBER);
        e.addTransition('0', eZero);

        State ePlusMinus = new State(false, TokenType.FLOAT_NUMBER);
        e.addTransition('+', ePlusMinus);
        e.addTransition('-', ePlusMinus);
        ePlusMinus.addTransition('0', eZero);

        State eNonzero = new State(true, TokenType.FLOAT_NUMBER);
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
        State id = new State(true, TokenType.ID);
        addLetterTransitionsWithException(initial, id, excludedChar);
        addAlphaNumTransitions(id, id);
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
        State m = new State(true, TokenType.ID);
        initial.addTransition('m', m);

        State ma = new State(true, TokenType.ID);
        m.addTransition('a', ma);
        addAlphaNumTransitionsWithException(m, m, new Character[]{'a'});

        State mai = new State(true, TokenType.ID);
        ma.addTransition('i', mai);
        addAlphaNumTransitionsWithException(ma, ma, new Character[]{'i'});

        State main = new State(true, TokenType.MAIN);
        mai.addTransition('n', main);

        State mainId = new State(true, TokenType.ID);
        addAlphaNumTransitions(main, mainId);
        addAlphaNumTransitions(mainId, mainId);
    }

    private static void constructP(State initial) {
        State p = new State(true, TokenType.ID);
        initial.addTransition('p', p);
        addAlphaNumTransitionsWithException(p,p,new Character[]{'r', 'u'});

        // private
        State pr = new State(true, TokenType.ID);
        p.addTransition('r', pr);

        State pri = new State(true, TokenType.ID);
        pr.addTransition('i', pri);
        addAlphaNumTransitionsWithException(pr, pr, new Character[]{'i'});

        State priv = new State(true, TokenType.ID);
        pri.addTransition('v', priv);
        addAlphaNumTransitionsWithException(pri,pri, new Character[]{'v'});

        State priva = new State(true, TokenType.ID);
        priv.addTransition('a', priva);
        addAlphaNumTransitionsWithException(priv, priv, new Character[]{'a'});

        State privat = new State(true, TokenType.ID);
        priva.addTransition('t', privat);
        addAlphaNumTransitionsWithException(priva,priva, new Character[]{'t'});

        State privateState = new State(true, TokenType.PRIVATE);
        privat.addTransition('e',privateState);
        addAlphaNumTransitionsWithException(privat, privat, new Character[]{'e'});

        State privateId = new State(true, TokenType.ID);
        addAlphaNumTransitions(privateState, privateId);
        addAlphaNumTransitions(privateId, privateId);

        // public
        State pu = new State(true, TokenType.ID);
        p.addTransition('u', pu);

        State pub = new State(true, TokenType.ID);
        pu.addTransition('b', pub);
        addAlphaNumTransitionsWithException(pu, pu, new Character[]{'b'});

        State publ = new State(true, TokenType.ID);
        pub.addTransition('l', publ);
        addAlphaNumTransitionsWithException(pub, pub, new Character[]{'l'});

        State publi = new State(true, TokenType.ID);
        publ.addTransition('i', publi);
        addAlphaNumTransitionsWithException(publ, publ, new Character[]{'i'});

        State publicState = new State(true, TokenType.PUBLIC);
        publi.addTransition('c',publicState);
        addAlphaNumTransitionsWithException(publi, publi, new Character[]{'c'});

        State publicId = new State(true, TokenType.ID);
        addAlphaNumTransitions(publicState, publicId);
        addAlphaNumTransitions(publicId, publicId);
    }

    private static void constructR(State initial){
        State r = new State(true, TokenType.ID);
        initial.addTransition('r', r);

        State re = new State(true, TokenType.ID);
        r.addTransition('e', re);
        addAlphaNumTransitionsWithException(r,r, new Character[]{'e'});

        // read
        State rea = new State(true, TokenType.ID);
        re.addTransition('a', rea);
        addAlphaNumTransitionsWithException(re, re, new Character[]{'a', 't'});

        State read = new State(true, TokenType.READ);
        rea.addTransition('d', read);
        addAlphaNumTransitionsWithException(rea, rea, new Character[]{'d'});

        State readId = new State(true, TokenType.ID);
        addAlphaNumTransitions(read, readId);
        addAlphaNumTransitions(readId, readId);

        // return
        State ret = new State(true, TokenType.ID);
        re.addTransition('t', ret);

        State retu = new State(true, TokenType.ID);
        ret.addTransition('u', retu);
        addAlphaNumTransitionsWithException(ret, ret, new Character[]{'u'});

        State retur = new State(true, TokenType.ID);
        retu.addTransition('r', retur);
        addAlphaNumTransitionsWithException(retu, retu, new Character[]{'r'});

        State returnState = new State(true, TokenType.RETURN);
        retur.addTransition('n', returnState);
        addAlphaNumTransitionsWithException(retur, retur, new Character[]{'n'});

        State returnId = new State(true, TokenType.ID);
        addAlphaNumTransitions(returnState, returnId);
        addAlphaNumTransitions(returnId, returnId);
    }

    private static void constructS(State initial) {
        State s = new State(true, TokenType.ID);
        initial.addTransition('s', s);

        // string
        State st = new State(true, TokenType.ID);
        s.addTransition('t', st);
        addAlphaNumTransitionsWithException(s, s, new Character[]{'t'});

        State str = new State(true, TokenType.ID);
        st.addTransition('r', str);
        addAlphaNumTransitionsWithException(st, st, new Character[]{'r'});

        State stri = new State(true, TokenType.ID);
        str.addTransition('i', stri);
        addAlphaNumTransitionsWithException(str, str, new Character[]{'i'});

        State strin = new State(true, TokenType.ID);
        stri.addTransition('n', strin);
        addAlphaNumTransitionsWithException(stri, stri, new Character[]{'n'});

        State string = new State(true, TokenType.STRING);
        strin.addTransition('g', string);
        addAlphaNumTransitionsWithException(strin, strin, new Character[]{'g'});

        State stringId = new State(true, TokenType.ID);
        addAlphaNumTransitions(string, stringId);
        addAlphaNumTransitions(stringId, stringId);
    }

    private static void constructT(State initial) {
        State t = new State(true, TokenType.ID);
        initial.addTransition('t', t);

        // then
        State th = new State(true, TokenType.ID);
        t.addTransition('h', th);
        addAlphaNumTransitionsWithException(t, t, new Character[]{'h'});

        State the = new State(true, TokenType.ID);
        th.addTransition('e', the);
        addAlphaNumTransitionsWithException(th, th, new Character[]{'e'});

        State then = new State(true, TokenType.THEN);
        the.addTransition('n', then);
        addAlphaNumTransitionsWithException(the, the, new Character[]{'n'});

        State thenId = new State(true, TokenType.ID);
        addAlphaNumTransitions(then, thenId);
        addAlphaNumTransitions(thenId, thenId);
    }

    private static void constructV(State initial) {
        State v = new State(true, TokenType.ID);
        initial.addTransition('v', v);
        addAlphaNumTransitionsWithException(v, v, new Character[]{'a', 'o'});

        // var
        State va = new State(true, TokenType.ID);
        v.addTransition('a', va);

        State var = new State(true, TokenType.VAR);
        va.addTransition('r', var);
        addAlphaNumTransitionsWithException(va, va, new Character[]{'r'});

        State varId = new State(true, TokenType.ID);
        addAlphaNumTransitions(var, varId);
        addAlphaNumTransitions(varId, varId);

        // void
        State vo = new State(true, TokenType.ID);
        v.addTransition('o', vo);

        State voi = new State(true, TokenType.ID);
        vo.addTransition('i', voi);
        addAlphaNumTransitionsWithException(vo, vo, new Character[]{'i'});

        State voidState = new State(true, TokenType.VOID);
        voi.addTransition('d', voidState);
        addAlphaNumTransitionsWithException(voi, voi, new Character[]{'d'});

        State voidId = new State(true, TokenType.ID);
        addAlphaNumTransitions(voidState, voidId);
        addAlphaNumTransitions(voidId, voidId);
    }

    private static void constructW(State initial) {
        State w = new State(true, TokenType.ID);
        initial.addTransition('w', w);
        addAlphaNumTransitionsWithException(w, w, new Character[]{'h', 'r'});

        // while
        State wh = new State(true, TokenType.ID);
        w.addTransition('h', wh);

        State whi = new State(true, TokenType.ID);
        wh.addTransition('i', whi);
        addAlphaNumTransitionsWithException(wh, wh, new Character[]{'i'});

        State whil = new State(true, TokenType.ID);
        whi.addTransition('l', whil);
        addAlphaNumTransitionsWithException(whi, whi, new Character[]{'l'});

        State whileState = new State(true, TokenType.WHILE);
        whil.addTransition('e', whileState);
        addAlphaNumTransitionsWithException(whil, whil, new Character[]{'e'});

        State whileId = new State(true, TokenType.ID);
        addAlphaNumTransitions(whileState, whileId);
        addAlphaNumTransitions(whileId, whileId);

        // write
        State wr = new State(true, TokenType.ID);
        w.addTransition('r', wr);

        State wri = new State(true, TokenType.ID);
        wr.addTransition('i', wri);
        addAlphaNumTransitionsWithException(wr, wr, new Character[]{'i'});

        State writ = new State(true, TokenType.ID);
        wri.addTransition('t', writ);
        addAlphaNumTransitionsWithException(wri, wri, new Character[]{'t'});

        State write = new State(true, TokenType.WRITE);
        writ.addTransition('e', write);
        addAlphaNumTransitionsWithException(writ, writ, new Character[]{'e'});

        State writeId = new State(true, TokenType.ID);
        addAlphaNumTransitions(write, writeId);
        addAlphaNumTransitions(writeId, writeId);
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

    private static void constructPunctuation(State initial) {
        State dot = new State(true, TokenType.DOT);
        initial.addTransition('.',dot);
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
        State stringStart = new State(false, TokenType.STRING_LITERAL);
        initial.addTransition('"', stringStart);

        addAlphaNumTransitions(stringStart, stringStart);
        stringStart.addTransition(' ', stringStart);

        State stringFinal = new State(true, TokenType.STRING_LITERAL);
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

}
