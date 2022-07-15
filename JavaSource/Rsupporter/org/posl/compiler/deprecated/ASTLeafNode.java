package org.posl.compiler.deprecated;

import org.posl.compiler.syntax.Symbol;
import org.posl.data.tokens.InputElement;

public class ASTLeafNode extends AbstractSyntaxTree{

    final InputElement i;

    public ASTLeafNode(Symbol e, InputElement i) {
        super(e);
        this.i = i;
    }

    public String toString(){
        return toString("");
    }
    
}
