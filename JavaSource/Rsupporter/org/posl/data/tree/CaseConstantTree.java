package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * Common tree node for case constants.
 * Patterns, constant expressions, {@code default} are allowed as case constants.
 * 
 * @author me 
 */

public interface CaseConstantTree extends Tree{
    
    static CaseConstantTree parse(JavaTokenManager src) throws ParsingException{
        if(src.match(JavaTS.DEFAULT)){
            return DefaultCaseLabelTree.parse(src);
        }
        return ExpressionTree.resolveConditionalExpression(src);
    }

}
