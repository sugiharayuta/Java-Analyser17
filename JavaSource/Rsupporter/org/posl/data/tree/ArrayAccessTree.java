package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for an array access expression.
 *
 * For example:
 * <pre>
 *   <em>expression</em> [ <em>index</em> ]
 * </pre>
 *
 * @jls 15.10.3 Array Access Expressions
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ArrayAccessTree(ExpressionTree expression, ExpressionTree index)implements ExpressionTree{

    static ArrayAccessTree parse(ExpressionTree expression, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_SQUARE_BRACKET);
        ExpressionTree index = ExpressionTree.parse(src);
        src.skip(JavaTS.RIGHT_SQUARE_BRACKET);
        return new ArrayAccessTree(expression, index);
    }

}
