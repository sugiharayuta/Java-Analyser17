package org.posl.compiler.deprecated;

import java.util.ArrayList;
import java.util.Collection;

import org.posl.compiler.syntax.Symbol;

public class ASTInnerNode extends AbstractSyntaxTree{

    final ArrayList<AbstractSyntaxTree> children = new ArrayList<>();

    public ASTInnerNode(Symbol e){
        super(e);
    }

    @Override
    public boolean accept(AbstractSyntaxTree node){
        return children.add(node);
    }

    @Override
    public boolean acceptAll(Collection<? extends AbstractSyntaxTree> c){
        return children.addAll(c);
    }

    @Override
    public String toString(){
        return toString("");
    }

    @Override
    protected String toString(String indent){
        String s = indent + this.s.toString() + "\n";
        for(var child : children){
            s += child.toString(indent + "    ");
        }
        return s;
    }

}
