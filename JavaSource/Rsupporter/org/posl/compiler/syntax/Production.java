package org.posl.compiler.syntax;

public record Production(Symbol left, SymbolSequence right){

    @Override
    public String toString(){
        return left.toString() + " -> " + right.toString();
    }

    public int hashCode(){
        int hash = 0;
        hash = hash * 31 + left.hashCode();
        hash = hash * 31 + right.hashCode();
        return hash;
    }

}