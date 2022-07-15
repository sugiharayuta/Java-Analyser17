package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for the conditional operator {@code ? :}.
 *
 * For example:
 * <pre>
 *   <em>condition</em> ? <em>trueExpression</em> : <em>falseExpression</em>
 * </pre>
 *
 * @jls 15.25 Conditional Operator ? :
 *
 * @author me
 */ 

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ConditionalExpressionTree(ExpressionTree condition, ExpressionTree trueExpression, ExpressionTree falseExpression) implements ExpressionTree{

    static ConditionalExpressionTree parse(ExpressionTree condition, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.QUESTION);
        ExpressionTree trueExpression = ExpressionTree.parse(src);
        src.skip(JavaTS.COLON);
        return new ConditionalExpressionTree(condition, trueExpression,
            ExpressionTree.followsLambdaExpression(src)? LambdaExpressionTree.parse(src) : ExpressionTree.resolveConditionalExpression(src));
    }

}
