package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;

public class IntegerLiteral extends Literal{

    final int radix;

    public IntegerLiteral(String text, Reference ref, int radix) {
        super(text, ref, JavaTS.INTEGER_LITERAL);
        this.radix = radix;
    }
    
    @Override
    public String info(){
        return "#" + toString() + "# (" +resolution.name()+"("+ radix +"))" + ref.toString();
    }

}
