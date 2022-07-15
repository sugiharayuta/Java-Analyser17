package org.posl.compiler;

import org.posl.compiler.syntax.Production;
import org.posl.compiler.syntax.SymbolSequence.Node;

public class LRItem{
    public static final LRItem NULL = new LRItem();

    public final Production rule;
    public final Node mark;
    public final LRItem prev;
    public final LRItem next;

    private LRItem(){
        this.rule = null;
        this.mark = null;
        this.prev = NULL;
        this.next = NULL;
    }

    public LRItem(Production p){
        this.rule = p;
        this.mark = rule.right().top.next;
        this.prev = NULL;
        if(pointsBottom()){
            this.next = NULL;
        }else{
            this.next = new LRItem(this);
        }
    }
    
    private LRItem(LRItem i){
        this.rule = i.rule;
        this.mark = i.mark.next;
        this.prev = i;
        if(pointsBottom()){
            this.next = NULL;
        }else{
            this.next = new LRItem(this);
        }
    }

    public boolean pointsBottom(){
        return mark == rule.right().bottom;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof LRItem i){
            return rule == i.rule && mark == i.mark;
        }else{
            return false;
        }
    }

    @Override
    public String toString(){
        String s = rule.left().toString() + " -> | ";
        for(Node n = rule.right().top.next; n != rule.right().bottom; n = n.next){
            if(n == mark){
                s += " # ";
            }
            s += n.s.toString() + " | ";
        }
        if(mark == rule.right().bottom){
            s += " # ";
        }
        return s;
    }

    public int hashCode(){
        int hash = 0;
        hash = hash * 31 + rule.hashCode();
        hash = hash * 31 + mark.hashCode();
        return hash;
    }

}
