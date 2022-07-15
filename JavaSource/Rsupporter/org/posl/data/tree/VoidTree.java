package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

public record VoidTree() implements TypeTree{

    static VoidTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.VOID);
        return new VoidTree();
    }
    
}
