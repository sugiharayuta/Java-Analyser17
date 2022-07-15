package org.posl.compiler;

import java.util.HashSet;
import java.util.Map;

import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.Symbol;
import org.posl.compiler.syntax.SymbolSequence;
import org.posl.compiler.syntax.Syntax;
import org.posl.compiler.syntax.SymbolSequence.Node;
import org.posl.data.tree.ParsingException;

class SymbolTable{

    final HashSet<Symbol> nullsSet;
    final SetMap<Symbol, Symbol> firstSet;

    public SymbolTable(Syntax syntax){
        try{
            this.nullsSet = getNullsSet(syntax);
            this.firstSet = getFirstSet(syntax);
        }catch(ParsingException e){
            System.out.println(e);
            throw new IllegalArgumentException();
        }
        
    }

    public HashSet<Symbol> first(Syntax syntax, Map.Entry<LRItem, HashSet<Symbol>> e, Node n) throws ParsingException{
        var set = new HashSet<Symbol>();
        for(; n != e.getKey().rule.right().bottom; n = n.next){
            if(syntax.isTerminal(n.s)){
                set.add(n.s);
                return set;
            }else if(syntax.isNonTerminal(n.s)){
                set.addAll(firstSet.get(n.s));
                if(!nullsSet.contains(n.s)){
                    return set;
                }
            }else{
                throw new IllegalSymbolException(n.s);
            }
        }
        set.addAll(e.getValue());
        return set;
    }

    /**
     * Gets set of nullable(can be reduced to an zero-length sequence) non-terminal symbols.
     * @return set of nullable non-terminal symbols
     */

    private HashSet<Symbol> getNullsSet(Syntax syntax){
        var set = new HashSet<Symbol>();
        boolean updated;
        do{
            updated = false;
            PRODUCTIONS: for(Production p : syntax.productions()){
                if(p.right().isEmpty()){
                    updated |= set.add(p.left());
                }else{
                    for(Node n = p.right().top.next; n != p.right().bottom; n = n.next){
                        if(!set.contains(n.s)){
                            continue PRODUCTIONS;
                        }
                    }
                    updated |= set.add(p.left());
                }
            }
        }while(updated);
        return set;
    }

    /**
     * Gets first-set of each non-terminal symbols.
     * @param nullsSet
     * @return mapping of non-terminal symbols and first-sets
     * @throws ParsingException thrown when an illegal symbol was found
     */

    private SetMap<Symbol, Symbol> getFirstSet(Syntax syntax) throws ParsingException{
        var map = new SetMap<Symbol, Symbol>();

        boolean updated;
        do{
            updated = false;
            for(Production p : syntax.productions()){
                SymbolSequence right = p.right();
                for(Node n = right.top.next; n != right.bottom; n = n.next){
                    if(syntax.isTerminal(n.s)){
                        updated |= map.register(p.left(), n.s);
                        break;
                    }else if(syntax.isNonTerminal(n.s)){
                        updated |= map.registerAll(p.left(), map.getSet(n.s));
                        if(!nullsSet.contains(n.s)){
                            break;
                        }
                    }else{
                        throw new IllegalSymbolException(n.s);
                    }
                }
            }
        }while(updated);
        return map;
    }
    
}