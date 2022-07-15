package org.posl.compiler.deprecated;

import java.util.function.BiPredicate;

import org.posl.compiler.IllegalSymbolException;
import org.posl.compiler.LRItem;
import org.posl.compiler.TokenList;
import org.posl.compiler.deprecated.LALRParser.Closure;
import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.Symbol;
import org.posl.compiler.syntax.Syntax;
import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tokens.InputElement;
import org.posl.data.tokens.Token;
import org.posl.data.tree.ParsingException;
import org.posl.util.functions.CEPredicate;

public class JavaStateMachine extends StateMachine{

    public JavaStateMachine(int numOfState, Syntax syntax, Closure state0) throws ParsingException {
        super(numOfState, syntax, state0);
    }

    @Override
    public AbstractSyntaxTree compile(TokenList tokens, /*for debug */ LALRParser cc) throws ParsingException{
        stateStack.clear();
        result.clear();
        tokenList = tokens;

        int posFix = 0;
        int line = 0;
        InputElement e;

        stateStack.push(0);

        while(true){
            if((e = skipAnnotation(tokenList)) instanceof Token t){
                Action a = actionTable[stateStack.getTopToken()].get(t.resolution);
                if(a == null){
                    if(t.resolution == JavaTS.BITWISE_SIGNED_RIGHT_SHIFT){
                        tokenList.split(new Token(">", t.ref.fix(-1)), new Token(">", t.ref));
                    }else if(t.resolution == JavaTS.BITWISE_UNSIGNED_RIGHT_SHIFT){
                        tokenList.split(new Token(">", t.ref.fix(-1)), new Token(">>", t.ref));
                    }else{
                        System.out.println("State :");
                        System.out.println(stateStack);
                        System.out.println();
                        System.out.println("Result :");
                        System.out.println(result);
                        System.out.println();
                        System.out.println(cc.stateMapForDebug.get(stateStack.getTopToken()).showInfo());
                        throw new ParsingException("Illegal token \""+t.text+"\".");
                    }
                    posFix++;
                    continue;
                }
                if(a.act()){
                    break;
                }
            }else{
                tokenList.get();
            }
            if(e.ref.line != line){
                line = e.ref.line;
                posFix = 0;
            }else{
                if(posFix > 0){
                    e.ref.fix(posFix);
                }
            }
        }

        tokenList.first();
        return result.get();

    }

    private InputElement skipAnnotation(TokenList tokenList) throws ParsingException{
        InputElement e = tokenList.refer();
        BiPredicate<InputElement, JavaTS> eq = (elm, s) -> elm instanceof Token t && t.resolution == s;
        if(eq.test(e, JavaTS.AT_SIGN)){
            if(eq.test(tokenList.lookAhead(2, t -> true)[1], JavaTS.INTERFACE)){
                return e;
            }
            tokenList.get();
            tokenList.get();
            if(eq.test(tokenList.refer(), JavaTS.LEFT_ROUND_BRACKET)){
                while(eq.test(tokenList.get(), JavaTS.RIGHT_ROUND_BRACKET));
            }
            e = tokenList.refer();   
        }
        return e;
    }

    @Override
    public void putState(Closure c, Syntax syntax) throws ParsingException{
        ActionTuple actionTuple = new ActionTuple(syntax.terminalSymbols());
        GotoTuple gotoTuple = new GotoTuple(syntax.nonTerminalSymbols());

        for(var e : c.forwardEdges.entrySet()){
            if(syntax.isNonTerminal(e.getKey())){
                gotoTuple.put(e.getKey(), e.getValue().id);
            }else if(syntax.isTerminal(e.getKey())){
                actionTuple.put(e.getKey(), new Shift(e.getValue().id, this::shift));
            }else{
                throw new IllegalSymbolException(e.getKey());
            }
        }
        for(LRItem key : c.keyItems){
            if(key.pointsBottom()){
                for(Symbol ts : c.lookAheadSet.getSet(key)){
                    actionTuple.put(ts, new Reduce(key.rule, this::reduce));
                }
            }else if(key.mark.s == Symbol.EOF){
                actionTuple.put(Symbol.EOF, new Accept());
            }
        }
        for(LRItem derived : c.derivedItems){
            if(derived.pointsBottom()){
                for(Symbol ts : c.lookAheadSet.getSet(derived)){
                    Action a = new Reduce(derived.rule, this::reduce);
                    if(actionTuple.get(ts) != null && (actionTuple.get(ts) instanceof Reduce r && r.p() != derived.rule)){
                        if(r.p().right().isEmpty()){
                            a = new ConflictedAction(r, a, this::needsEmptyReduction);
                        }else if(((Reduce)a).p().right().isEmpty()){
                            a = new ConflictedAction((Reduce)a, r, this::needsEmptyReduction);
                        }
                    }else if(actionTuple.get(ts) != null && (actionTuple.get(ts) instanceof Shift s)){
                        if(((Reduce)a).p().right().isEmpty()){
                            a = new ConflictedAction((Reduce)a, s, this::needsEmptyReduction);
                        }else{
                            throw new ParsingException("Unresolved conflict occured.");
                        }
                    }
                    actionTuple.put(ts, a);
                }
            }else if(derived.mark.s == Symbol.EOF){
                actionTuple.put(Symbol.EOF, new Accept());
            }
        }
        actionTable[c.id] = actionTuple;
        gotoTable[c.id] = gotoTuple;
    }

    boolean needsEmptyReduction(Production p){
        return result.getTopToken().s.equals(p.left());
    }

    record ConflictedAction(Reduce emptyReduction, Action alternative, CEPredicate<Production> cond)implements Action{

        @Override
        public boolean act() throws ParsingException{
            if(cond.test(emptyReduction.p())){
                emptyReduction.act();
            }else{
                alternative.act();
            }
            return false;
        }

    }

}
