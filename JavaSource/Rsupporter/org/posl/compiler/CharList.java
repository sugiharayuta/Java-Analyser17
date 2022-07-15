package org.posl.compiler;

import java.util.Set;

import org.posl.data.tree.ParsingException;
import org.posl.util.DoublyLinkedList;
import org.posl.util.functions.CEPredicate;

/**
 * This is a doubly-linked list of characters with an private pointer.
 * It supports some operations which are used in the lexer.
 * @author me
 */

class CharList extends DoublyLinkedList<Character>{
    
    /**
     * Creates CharList of the specified characters.
     */
    public CharList(char... initialChars){
        for(char c : initialChars){
            add(c);
        }
    }

    /**
     * Reads back the specified number of characters from the current pointer.
     */
    public final String readBack(int size){
        String s = "";
        Node curr = current;
        for(int i = 0; i < size; i++){
            if(curr == top) return s;
            s = curr.e + s;
            curr = curr.prev;
        }
        return s;
    }

    /**
     * Reads back the characters which satisfies the specified condition from the current pointer.
     */
    public final String readBack(CEPredicate<Character> cond) throws ParsingException{
        String s = "";
        Node curr = current;
        while(cond.test(curr.e)){
            if(curr == top) return s;
            s = curr.e + s;
            curr = curr.prev;
        }
        return s;
    }

    /**
     * Reads back the characters which are contained in the specified sets from the current pointer.
     */
    @SafeVarargs
    public final String readBack(Set<Character>... charSets) throws ParsingException{
        return readBack(c ->
        {
            for(Set<Character> set : charSets){
                if(set.contains(c)) return true;
            }
            return false;
        });
    }

    /**
     * Reads back the characters which are contained in the specified sets from the current pointer.
     */
    public final String readForward(int size){
        String s = "";
        for(int i = 0; i < size && hasNext(); i++){
            s += get();
        }
        return s;
    }

    /**
     * Reads the characters which satisfies the specified condition from the current pointer.
     */
    public final String readForward(CEPredicate<Character> cond) throws ParsingException{
        String s = "";
        while(hasNext() && cond.test(current.e)){
            s += get();
        }
        return s;
    }

    /**
     * Reads the characters which are contained in the specified sets from the current pointer.
     */
    @SafeVarargs
    public final String readForward(Set<Character>... charSets) throws ParsingException{
        return readForward(c ->
        {
            for(Set<Character> set : charSets){
                if(set.contains(c)) return true;
            }
            return false;
        });
    }

}