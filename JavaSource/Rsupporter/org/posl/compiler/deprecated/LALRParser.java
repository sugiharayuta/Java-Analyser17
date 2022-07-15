package org.posl.compiler.deprecated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;

import org.posl.compiler.IllegalSymbolException;
import org.posl.compiler.LRItem;
import org.posl.compiler.SetMap;
import org.posl.compiler.syntax.ImplicitNTS;
import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.Symbol;
import org.posl.compiler.syntax.SymbolSequence;
import org.posl.compiler.syntax.Syntax;
import org.posl.compiler.syntax.SymbolSequence.Node;
import org.posl.data.tree.ParsingException;
import org.posl.io.Logger;

/**
 * @author me
 */

public abstract class LALRParser{

    protected final Syntax syntax;
    private final HashSet<LRItem> initial;
    private final HashMap<LRItem, Closure> stateTable = new HashMap<>();
    private final SymbolTable symbolTable;
    private final SetMap<Symbol, LRItem> itemMap;

    protected final int numOfState;
    protected final Closure state0;
    
    public final HashMap<Integer, Closure> stateMapForDebug = new HashMap<>();
    public final Logger debugLogger = Logger.MASTER;

    public LALRParser(Syntax syntax) throws ParsingException{
        this.syntax = syntax;
        this.initial = new HashSet<>();
        for(Symbol finalSymbol : syntax.finalSymbols()){
            initial.add(new LRItem(new Production(ImplicitNTS.GOAL, new SymbolSequence(syntax.topSymbol(), finalSymbol))));  
        }
        this.symbolTable = new SymbolTable();
        this.itemMap = getLR0Items();

        /* I don't suppose this constructor is called from multiple threads, though! */
        synchronized(Closure.class){
            this.state0 = addState(initial);
            this.numOfState = Closure.idSupplier - state0.id;
        }

        var initialMap = new SetMap<LRItem, Symbol>();
        for(LRItem i : initial){
            initialMap.put(i, new HashSet<>());
        }
        this.state0.getLookAheadSet(initialMap, itemMap, symbolTable);
    }

    public abstract StateMachine getStateMachine() throws ParsingException;

    /**
     * Produces LR0 items from context-free grammer productions.
     * The items are classified in its lefthand non-terminal symbols, and they can be
     * accesed over a HashMap whose key is the lefthand.
     * @return mapping of LR0Items
     */

    private SetMap<Symbol, LRItem> getLR0Items(){
        var itemMap = new SetMap<Symbol, LRItem>();
        for(Production p : syntax.productions()){
            var item = new LRItem(p);
            if(itemMap.containsKey(p.left())){
                itemMap.get(p.left()).add(item);
            }else{
                var items = new HashSet<LRItem>();
                items.add(item);
                itemMap.put(p.left(), items);
            }
        }
        return itemMap;
    }

    /**
     * Constructs a state with clustering LR0 items.
     * @param keyItems
     * @return
     */

    private Closure addState(HashSet<LRItem> keyItems) throws ParsingException{
        var derivedItems = new HashSet<LRItem>();

        deriveItems(derivedItems, keyItems);

        var state = new Closure(keyItems, derivedItems);
        for(LRItem k : keyItems){
            stateTable.put(k, state);
        }

        var population = new ArrayList<LRItem>();
        population.addAll(keyItems);
        population.addAll(derivedItems);

        population.removeIf(item -> item.pointsBottom());
        while(!population.isEmpty()){
            LRItem item = population.get(0);
            Symbol s = item.mark.s;
            var key = new HashSet<LRItem>();
            var checked = new HashSet<LRItem>();
            for(LRItem i : population){
                if(i.mark.s == item.mark.s){
                    key.add(i.next);
                    checked.add(i);
                }
            }

            Closure c;
            if((c = stateTable.get(item.next)) != null && c.keyItems.containsAll(key) && key.containsAll(c.keyItems)){
                state.drawEdge(s, c);
            }else{
                state.drawEdge(s, addState(key));
            }
            population.removeAll(checked);
        }
        stateMapForDebug.put(state.id, state);
        return state;
    }

    /**
     * It derives all LR items derived from input LR items.
     */

    private void deriveItems(HashSet<LRItem> result, HashSet<LRItem> input) throws ParsingException{
        for(LRItem item : new HashSet<>(input)){

            if(!item.pointsBottom() && syntax.isNonTerminal(item.mark.s)){
                Symbol nts = item.mark.s;
                HashSet<LRItem> set = itemMap.get(nts);
                if(set == null){
                    System.out.println("The non-terminal symbol \""+nts.toString()+"\" cannot be reduced to a terminal symbol.");
                    throw new ParsingException("The input syntax is incomplete.");
                }
                var added = new HashSet<LRItem>(set);
                added.removeAll(result);
                result.addAll(added);
                deriveItems(result, added);
            }
        }
    }

    public HashSet<Symbol> getNullsSet(){
        return symbolTable.nullsSet;
    }

    public Set<Entry<Symbol, HashSet<Symbol>>> getFirstSet(){
        return symbolTable.firstSet.entrySet();
    }

    public class Closure{
        static int idSupplier = 0;
        public final int id;
        final HashMap<Symbol, Closure> forwardEdges = new HashMap<>();
        final HashMap<Symbol, Closure> reverseEdges = new HashMap<>();
        final HashSet<LRItem> keyItems;
        final HashSet<LRItem> derivedItems;
        final SetMap<LRItem, Symbol> lookAheadSet = new SetMap<>();
    
        {
            id = idSupplier++;
        }
    
        Closure(HashSet<LRItem> keyItems, HashSet<LRItem> derivedItems){
            this.keyItems = keyItems;
            this.derivedItems = derivedItems;
        }
    
        public String info(){
            Function<HashSet<LRItem>, String> converter = set ->
            {
                String s = "";
                for(LRItem i : set){
                    s += i.toString() + "\n";
                }
                return s;
            };
            return "key:\n" + converter.apply(keyItems) + "\n"
                + "derived:\n" + converter.apply(derivedItems) + "\n"
                + "edges: \n" + "\n";
        }
    
        public void drawEdge(Symbol s, Closure c){
            if(forwardEdges.get(s) == null){
                forwardEdges.put(s, c);
            }
            if(c.reverseEdges.get(s) == null){
                c.reverseEdges.put(s, this);
            }
        }
    
        public boolean getLookAheadSet(SetMap<LRItem, Symbol> las, SetMap<Symbol, LRItem> itemMap, SymbolTable table) throws ParsingException{
            boolean updated = this.lookAheadSet.merge(las);
            updated |= deriveLookAheadSet(new HashSet<>(keyItems), new HashSet<>(keyItems), itemMap, table);
            
            if(updated){
                for(Closure next : forwardEdges.values()){
                    var introduced = new SetMap<LRItem, Symbol>();
                    for(LRItem key : next.keyItems){
                        introduced.registerAll(key, lookAheadSet.getSet(key.prev));
                    }
                    next.getLookAheadSet(introduced, itemMap, table);
                }
            }
            return updated;
        }
    
        /**
         * Puts new mapping of LR items and look-ahead sets from already-derived mapping.
         * NOTE that first two parameters are MUTABLE. DO NOT apply arguments which are supposed be immutable to these parameters.
         * @param searched already refered LR items (MUTABLE)
         * @param input LR items used to derive new mapping (MUTABLE)
         * @param itemMap mapping of non-terminal symbols and LR items
         * @param table table of non-terminal symbols and first-sets
         */
    
        private boolean deriveLookAheadSet(HashSet<LRItem> searched, HashSet<LRItem> input, SetMap<Symbol, LRItem> itemMap, SymbolTable table) throws ParsingException{
            boolean updated = false;
    
            for(LRItem item : new HashSet<>(input)){
                var las = new HashSet<Symbol>();
                UNTIL_BOTTOM: if(!item.pointsBottom()){
                    for(Node n = item.mark.next; n != item.rule.right().bottom; n = n.next){
                        if(syntax.isTerminal(n.s)){
                            las.add(n.s);
                            break UNTIL_BOTTOM;
                        }else if(syntax.isNonTerminal(n.s)){
                            las.addAll(table.firstSet.get(n.s));
                            if(!table.nullsSet.contains(n.s)){
                                break UNTIL_BOTTOM;
                            }
                        }else{
                            throw new IllegalSymbolException(n.s);
                        }
                    }
                }
                las.addAll(lookAheadSet.get(item));
    
                if(!item.pointsBottom() && syntax.isNonTerminal(item.mark.s)){
                    Symbol nts = item.mark.s;
                    HashSet<LRItem> set = itemMap.get(nts);
                    if(set == null){
                        System.out.println("The non-terminal symbol \""+nts.toString()+"\" cannot be reduced to a terminal symbol.");
                        throw new ParsingException("The input syntax is incomplete.");
                    }
                    var added = new HashSet<LRItem>(set);
                    for(LRItem i : added){
                        updated |= lookAheadSet.registerAll(i, las);
                    }
                    searched.addAll(added);
                    if(updated){
                        deriveLookAheadSet(searched, added, itemMap, table);
                    }
                }
            }
            return updated;
        }
    
        public String showInfo(){
            Function<HashSet<LRItem>, String> rule = c ->
            {
                String enm = "";
                for(var e : c){
                    enm += e.toString() +" "+lookAheadSet.get(e).toString()+"\n";
                }
                return enm;
            };
            return "s" + id + "\nkey :\n" + rule.apply(keyItems) + "\nderived :\n" + rule.apply(derivedItems);
        }
    
        public String showTransit(){
            Function<HashSet<LRItem>, String> rule = c ->
            {
                String enm = "";
                for(var e : c){
                    enm += e.toString() +" "+lookAheadSet.get(e).toString()+"\n";
                }
                return enm;
            };
            return "s" + id + "\nkey :\n" + rule.apply(keyItems) + "\nderived :\n" + rule.apply(derivedItems);
        }
    
        @Override
        public String toString(){
            return "s" + id;
        }
    
        @Override
        public int hashCode(){
            return id;
        }
        
        @Override
        public boolean equals(Object o){
            if(o instanceof Closure c){
                if(c.keyItems.equals(keyItems)){
                    return true;
                }
            }
            return false;
        }
    
    }
    
    class SymbolTable{

        final HashSet<Symbol> nullsSet;
        final SetMap<Symbol, Symbol> firstSet;

        public SymbolTable() throws ParsingException{
            this.nullsSet = getNullsSet();
            this.firstSet = getFirstSet();
        }

        public HashSet<Symbol> first(Map.Entry<LRItem, HashSet<Symbol>> e, Node n) throws ParsingException{
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

        private HashSet<Symbol> getNullsSet(){
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

        private SetMap<Symbol, Symbol> getFirstSet() throws ParsingException{
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
     
}
