package org.posl.data.tokens;

import org.posl.data.resolution.PrimitiveType;

public class BooleanLiteral extends Literal{

    public BooleanLiteral(boolean tf, Reference ref) {
        super(String.valueOf(tf), ref);
        this.type = PrimitiveType.BOOLEAN;
    }
    
}
