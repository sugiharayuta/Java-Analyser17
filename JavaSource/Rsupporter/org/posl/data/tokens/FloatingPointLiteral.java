package org.posl.data.tokens;

import org.posl.compiler.syntax.javalang.JavaTS;

public class FloatingPointLiteral extends Literal{
    final int radix;

    public FloatingPointLiteral(String text, Reference ref, int radix){
        super(text, ref, JavaTS.FLOATING_POINT_LITERAL);
        this.radix = radix;
    }
    
    @Override
    public String info(){
        return "#" + toString() + "# (" +resolution.name()+"("+ radix +"))" + ref.toString();
    }

}
