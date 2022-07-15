package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code throw} statement.
 *
 * For example:
 * <pre>
 *   throw <em>expression</em>;
 * </pre>
 *
 * @jls 14.18 The throw Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ThrowTree(ExpressionTree expression) implements StatementTree{

    static ThrowTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.THROW);
        ExpressionTree expression = ExpressionTree.parse(src);
        src.skip(JavaTS.SEMICOLON);
        return new ThrowTree(expression);
    }
    
}
