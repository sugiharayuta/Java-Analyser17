package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code return} statement.
 *
 * For example:
 * <pre>
 *   return;
 *   return <em>expression</em>;
 * </pre>
 *
 * @jls 14.17 The return Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ReturnTree(ExpressionTree expression) implements StatementTree{
    
    static ReturnTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.RETURN);
        ExpressionTree expression = null;
        if(!src.match(JavaTS.SEMICOLON)){
            expression = ExpressionTree.parse(src);
        }
        src.skip(JavaTS.SEMICOLON);
        return new ReturnTree(expression);
    }
    
}
