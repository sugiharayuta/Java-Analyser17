package org.posl.data.tokens;

import org.posl.data.tree.TypeTree;

public class NullLiteral extends Literal{

    public NullLiteral(Reference ref) {
        super("null", ref);
        this.type = TypeTree.NULL;
    }
    
}
