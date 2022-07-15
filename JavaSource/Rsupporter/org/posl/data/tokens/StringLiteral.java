package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.TypeTree;

public class StringLiteral extends Literal{

    public StringLiteral(String text, Reference ref) {
        super(text, ref, JavaTS.STRING_LITERAL);
        this.type = TypeTree.STRING;
    }

    @Override
    public String toString(){
        return "\"" + text + "\"";
    }
    
}
