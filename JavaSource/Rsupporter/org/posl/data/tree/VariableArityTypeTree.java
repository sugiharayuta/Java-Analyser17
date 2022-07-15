package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

public record VariableArityTypeTree(TypeTree type) implements TypeTree{

    static VariableArityTypeTree parse(TypeTree elementType, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.ELLIPSIS);
        return new VariableArityTypeTree(elementType);
    }

}
