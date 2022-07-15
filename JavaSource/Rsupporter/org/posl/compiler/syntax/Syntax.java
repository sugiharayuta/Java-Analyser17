package org.posl.compiler.syntax;

import java.util.HashSet;

/**
 * @author me
 */
public interface Syntax{

    public HashSet<Production> productions();
    
    /**
     * Specifies the goal symbol of the syntax.
     * @return goal symbol
     */

    public Symbol topSymbol();

    public HashSet<Symbol> nonTerminalSymbols();

    public HashSet<Symbol> terminalSymbols();

    public default boolean isNonTerminal(Symbol s){
        return nonTerminalSymbols().contains(s);
    }

    public default boolean isTerminal(Symbol s){
        return terminalSymbols().contains(s);
    }

    public HashSet<Symbol> finalSymbols();

    public default HashSet<Symbol> symbols(){
        var set = new HashSet<Symbol>(nonTerminalSymbols());
        set.addAll(terminalSymbols());
        return set;
    }

}
