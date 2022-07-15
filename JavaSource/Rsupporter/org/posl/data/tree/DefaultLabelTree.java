package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A case label that marks {@code default}
 * 
 * @since 17
 */
public record DefaultLabelTree() implements SwitchLabelTree{

    static DefaultCaseLabelTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.DEFAULT);
        
        return new DefaultCaseLabelTree();
    }

}


