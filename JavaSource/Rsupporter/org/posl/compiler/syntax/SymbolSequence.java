package org.posl.compiler.syntax;

public class SymbolSequence{
    public final Node top = new Node(null, null, null);
    public final Node bottom = new Node(null, null, null);

    {
        top.next = bottom;
        bottom.prev = top;
    }
    
    /**
     * Creates linked list of the specified elements.
     */
    @SafeVarargs
    public SymbolSequence(Symbol... initials){
        for(Symbol s : initials){
            Node n = new Node(s, bottom.prev, bottom);
            bottom.prev.next = n;
            bottom.prev = n;
        }
    }

    public boolean isEmpty(){
        return top.next == bottom;
    }

    @Override
    public String toString(){
        Node current = top.next;
        String s = "[";
        while(current != bottom){
            s += current.s.toString();
            current = current.next;
            if(current == bottom){
                break;
            }
            s += ", ";
        }
        s += "]";
        return s;
    }

    public int hashCode(){
        int hash = 0;
        for(Node n = top.next; n != bottom; n = n.next){
            hash = hash * 31 + ((n.s == null)? 0 : n.s.hashCode());
        }
        return hash;
    }

    /**
     * Nodes which are used in the sequence. 
     */
    public class Node{
        public final Symbol s;
        public Node prev;
        public Node next;

        public Node(Symbol s, Node prev, Node next){
            this.s = s;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public int hashCode(){
            int hash = 0;
            if(this.prev != null){
                hash += prev.hashCode() * 31;
            }
            if(this.s != null){
                hash += this.s.hashCode();
            }
            return hash;
        }
    }
}
