package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.ModifiersTree.Modifier;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;
/**
 * A tree node for an annotation.
 *
 * For example:
 * <pre>
 *    {@code @}<em>annotationType</em>
 *    {@code @}<em>annotationType</em> ( <em>arguments</em> )
 * </pre>
 *
 * @jls 9.7 Annotations
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record AnnotationTree(ExpressionNameTree annotationType, ArrayList<ElementValuePairTree> arguments)implements ExpressionTree, Modifier{

    static AnnotationTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.AT_SIGN);
        ExpressionNameTree annotationType = ExpressionNameTree.parse(src);
        ArrayList<ElementValuePairTree> arguments = new ArrayList<>();
        if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET);
            arguments.addAll(Tree.getList(ElementValuePairTree::parse, JavaTS.RIGHT_ROUND_BRACKET, src));
            src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        }
        return new AnnotationTree(annotationType, arguments);
    }
    
}
