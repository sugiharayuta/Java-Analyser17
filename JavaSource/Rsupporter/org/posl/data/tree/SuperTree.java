package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

public record SuperTree(Tree qualifier)implements ExpressionTree{

    static SuperTree parse(Tree qualifier, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.SUPER);
        return new SuperTree(qualifier);
    }   
}