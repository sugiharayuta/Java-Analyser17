package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code yield} statement.
 *
 * For example:
 * <pre>
 *   yield <em>expression</em> ;
 * </pre>
 *
 * @jls 14.21 The yield Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.MAYBE_OK)
public record YieldTree(ExpressionTree value) implements StatementTree{

    static YieldTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.YIELD);
        var value = ExpressionTree.parse(src);
        src.skip(JavaTS.SEMICOLON);
        return new YieldTree(value);
    }

}
