package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

public record VarTree() implements TypeTree{

    static VarTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.VAR);
        return new VarTree();
    }
    
}
