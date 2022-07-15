package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A marker interface for {@code Tree}s that may be used as {@link CaseTree} labels.
 *
 * @since 17
 */

public record CaseLabelTree(ArrayList<CaseConstantTree> constants, ExpressionTree guardedPattern) implements SwitchLabelTree{

    static CaseLabelTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.CASE);
        ArrayList<CaseConstantTree> constants = new ArrayList<>();
        ExpressionTree guardedPattern = null;
        if(PatternTree.followsPattern(src)){
            constants.add(PatternTree.parse(src));
            if(src.match(JavaTS.WHEN)){
                src.skip(JavaTS.WHEN);
                guardedPattern = ExpressionTree.parse(src);
            }
        }else{
            constants = Tree.getListWithoutBracket(CaseConstantTree::parse, JavaTS.COMMA, src);
        }
        return new CaseLabelTree(constants, guardedPattern);
    }

}
