package org.posl.compiler;

import java.util.Set;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tokens.InputElement;
import org.posl.data.tokens.Token;
import org.posl.data.tree.FatalParserError;
import org.posl.data.tree.ParsingException;
import org.posl.util.DoublyLinkedList;
import org.posl.util.functions.CEPredicate;

/**
 * This is a singly-linked list with a pointer.
 * 
 * @author me
 */
public class TokenList extends DoublyLinkedList<InputElement>{

    /**
     * Creates CharList of the specified characters.
     */
    public TokenList(InputElement... initialTokens){
        for(InputElement e : initialTokens){
            add(e);
        }
    }

    @SafeVarargs
    public final InputElement[] referWithCondition(CEPredicate<InputElement>... conds) throws ParsingException{
        var elements = new InputElement[conds.length];
        Node curr = current;
        for(int i = 0; i < conds.length; i++){
            while(!conds[i].test(curr.e)){
                if(curr == bottom){
                    var ret = new InputElement[i];
                    for(int j = 0; j < i; j++){
                        ret[j] = elements[j];
                    }
                    return ret;
                }
                curr = curr.next;
            }
            elements[i] = curr.e;
        }
        return elements;
    }

    public final InputElement[] lookAhead(int length, CEPredicate<InputElement> cond) throws ParsingException{
        var elements = new InputElement[length];
        Node curr = current;
        for(int i = 0; i < length; i++){
            while(!cond.test(curr.e)){
                if(curr == bottom){
                    var ret = new InputElement[i];
                    for(int j = 0; j < i; j++){
                        ret[j] = elements[j];
                    }
                    return ret;
                }
                curr = curr.next;
            }
            elements[i] = curr.e;
            curr = curr.next;
        }
        return elements;
    }

    /**
     * Reads back the characters which satisfies the specified condition from the current pointer.
     */
    @SafeVarargs
    public final boolean applyTests(CEPredicate<InputElement>... testers) throws ParsingException{
        Node curr = current;
        for(CEPredicate<InputElement> cond : testers){
            if(curr == top || !cond.test(curr.e)){
                return false;
            }
            curr = curr.prev;
        }
        return true;
    }



    public final void split(TokenPointer ptr, InputElement e1, InputElement e2) throws ParsingException{
        if(ptr.pointer == top || ptr.pointer.prev == top){
            throw new FatalParserError("Illegal token operation : Cannot apply 'split'.");
        }
        Node curr =  ptr.pointer;
        curr = curr.prev;
        curr.next = new Node(e1, curr, null);
        curr = curr.next;
        curr.next = new Node(e2, curr, ptr.pointer.next);
        ptr.pointer.next.prev = curr.next;
        ptr.pointer = curr;
    }

    public final void split(InputElement e1, InputElement e2) throws ParsingException{
        if(current == top || current.prev == top){
            throw new FatalParserError("Illegal token operation : Cannot apply 'split'.");
        }
        Node curr =  current;
        curr = curr.prev;
        curr.next = new Node(e1, curr, null);
        curr = curr.next;
        curr.next = new Node(e2, curr, current.next);
        current.next.prev = curr.next;
        current = curr;
    }

    public class NodeAccessor{
        private Node n;

        public NodeAccessor(){
            this.n = current;
        }
        
        public InputElement curr(){
            return n.e;
        }

        public void next(){
            n = n.next;
        }
    }

    public final class TokenPointer implements Cloneable{
        private Node pointer;

        public TokenPointer() throws ParsingException{
            Node curr = current;
            while(!(curr.e instanceof Token)){
                curr = curr.next;
            }
            this.pointer = curr;
        }

        private TokenPointer(Node pointer){
            this.pointer = pointer;
        }

        public Token element() throws ParsingException{
            return (Token)pointer.e;
        }

        public void next() throws ParsingException{
            do{
                pointer = pointer.next;
            }while(!(pointer.e instanceof Token));
        }

        public boolean hasNext(){
            return pointer != bottom;
        }

        public boolean match(JavaTS... symbols) throws ParsingException{
            Node curr = pointer;
            for(int i = 0, n = symbols.length; i < n && curr != bottom; i++){
                if(symbols[i] != ((Token)curr.e).resolution){
                    return false;
                }
                do{
                    curr = curr.next;
                }while(!(curr.e instanceof Token));
            }
            return true;
        }

        public boolean match(Set<JavaTS> symbols) throws ParsingException{
            return symbols.contains(element().resolution);
        }

        public boolean match(int pos, JavaTS symbol) throws ParsingException{
            Node curr = pointer;
            for(int i = 0; i < pos && curr != bottom; i++){
                do{
                    curr = curr.next;
                }while(!(curr.e instanceof Token));
            }
            return ((Token)curr.e).resolution == symbol;
        }

        public boolean match(int pos, Set<JavaTS> symbols) throws ParsingException{
            Node curr = pointer;
            for(int i = 0; i < pos && curr != bottom; i++){
                do{
                    curr = curr.next;
                }while(!(curr.e instanceof Token));
            }
            return symbols.contains(((Token)curr.e).resolution);
        }

        public void recover(TokenPointer ptr){
            this.pointer = ptr.pointer;
        }

        public void setProvisionalToken(JavaTS symbol){
            Token t = new Token(symbol.name(), null);
            pointer = new Node(t, null, pointer);
        }

        @Override
        public TokenPointer clone(){
            return new TokenPointer(pointer);
        }

    }

}
