package org.posl.compiler.deprecated;

import java.util.Collection;

import org.posl.compiler.syntax.Symbol;

public class AbstractSyntaxTree{

    Symbol s;

    public AbstractSyntaxTree(Symbol s) {
        this.s = s;
    }

    public boolean accept(AbstractSyntaxTree node){
        throw new UnsupportedOperationException();
    }

    public boolean acceptAll(Collection<? extends AbstractSyntaxTree> c){
        throw new UnsupportedOperationException();
    }

    protected String toString(String indent){
        return indent + s.toString() + "\n";
    }

}
