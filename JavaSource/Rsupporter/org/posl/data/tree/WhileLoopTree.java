package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code while} loop statement.
 *
 * For example:
 * <pre>
 *   while ( <em>condition</em> )
 *     <em>statement</em>
 * </pre>
 *
 *
 * @jls 14.12 The while Statement
 *
 * @author me
 */


@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record WhileLoopTree(ExpressionTree condition, StatementTree statement) implements StatementTree{

    static WhileLoopTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.WHILE, JavaTS.LEFT_ROUND_BRACKET);
        ExpressionTree condition = ExpressionTree.parse(src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        return new WhileLoopTree(condition, StatementTree.parse(src));
    }

}
