package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A case label that marks {@code default}.
 * 
 * @author me
 */
public record DefaultCaseLabelTree() implements SwitchLabelTree, CaseConstantTree{

    static DefaultCaseLabelTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.DEFAULT);
        return new DefaultCaseLabelTree();
    }

}


