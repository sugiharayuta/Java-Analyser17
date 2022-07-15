package org.posl.compiler.deprecated;

import java.util.ArrayList;
import java.util.HashSet;

import org.posl.compiler.IllegalSymbolException;
import org.posl.compiler.LRItem;
import org.posl.compiler.TokenList;
import org.posl.compiler.deprecated.LALRParser.Closure;
import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.Symbol;
import org.posl.compiler.syntax.Syntax;
import org.posl.compiler.syntax.SymbolSequence.Node;
import org.posl.data.tokens.Token;
import org.posl.data.tree.ParsingException;
import org.posl.io.Logger;
import org.posl.util.RestrictedKeyHashTable;
import org.posl.util.SimpleStack;

public abstract class StateMachine{
    protected TokenList tokenList;
    final SimpleStack<Integer> stateStack = new SimpleStack<>();
    final SimpleStack<AbstractSyntaxTree> result = new SimpleStack<>();

    final ActionTuple[] actionTable;
    final GotoTuple[] gotoTable;
    
    public StateMachine(int numOfState, Syntax syntax, Closure state0) throws ParsingException{
        this.actionTable = new ActionTuple[numOfState];
        this.gotoTable = new GotoTuple[numOfState];
        fillTable(state0, syntax);
    }

    public abstract AbstractSyntaxTree compile(TokenList tokens, LALRParser parser) throws ParsingException;

    private void fillTable(Closure state0, Syntax syntax) throws ParsingException{
        boolean[] searched = new boolean[actionTable.length];
        var searching = new HashSet<Closure>();
        searching.add(state0);
        while(!searching.isEmpty()){
            var added = new HashSet<Closure>();
            for(Closure c : searching){
                putState(c, syntax);
                searched[c.id] = true;
                for(Closure next : c.forwardEdges.values()){
                    if(!searched[next.id]){
                        added.add(next);
                    }
                }
            }
            searching = added;
        }
    }

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
                    if(actionTuple.get(ts) != null && (actionTuple.get(ts) instanceof Reduce r && r.p() != derived.rule)){
                        Logger.MASTER.get("A reduce/reduce conflict has occured at action["+c.id+", "+ts+"].");
                        Logger.MASTER.get(r+" : r "+derived.rule);
                    }else if(actionTuple.get(ts) != null && (actionTuple.get(ts) instanceof Shift s)){
                        Logger.MASTER.get("A shift/reduce conflict has occured at action["+c.id+", "+ts+"].");
                        Logger.MASTER.get(s+" : r "+derived.rule);
                    }
                    actionTuple.put(ts, new Reduce(derived.rule, this::reduce));
                }
            }else if(derived.mark.s == Symbol.EOF){
                actionTuple.put(Symbol.EOF, new Accept());
            }
        }

        actionTable[c.id] = actionTuple;
        gotoTable[c.id] = gotoTuple;
    }

    public void shift(final int s){
        stateStack.push(s);
        Token t = (Token)tokenList.get();
        result.push(new ASTLeafNode(t.resolution, t));
    }

    public void reduce(final Production p){
        var set = new ArrayList<AbstractSyntaxTree>();
        Logger.MASTER.get("\n");
        for(Node n = p.right().bottom.prev; n != p.right().top; n = n.prev){
            AbstractSyntaxTree treeNode;
            set.add(0, treeNode = result.pop());
            Logger.MASTER.get(n.s+" : "+treeNode.s);
            stateStack.pop();
        }
        AbstractSyntaxTree node = new ASTInnerNode(p.left());
        result.push(node);
        stateStack.push(gotoTable[stateStack.getTopToken()].get(p.left()));
        Logger.MASTER.get("reduced to "+p.left());
    }

    class ActionTuple extends RestrictedKeyHashTable<Symbol, Action>{
    
        protected ActionTuple(final HashSet<Symbol> terminalSymbols){
            super(terminalSymbols, Symbol[]::new, Action[]::new);
        }

    }

    class GotoTuple extends RestrictedKeyHashTable<Symbol, Integer>{

        protected GotoTuple(final HashSet<Symbol> nonTerminalSymbols){
            super(nonTerminalSymbols, Symbol[]::new, Integer[]::new);
        }

    }

}
