package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a class literal expression.
 * For example:
 * <pre>
 *   <em>String</em>.<em>clasa</em>
 * </pre>
 *
 * @jls 15.8.2 Class Literals
 *
 * @author me;
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ClassLiteralTree(Tree type) implements ExpressionTree{

    static ClassLiteralTree parse(Tree type, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.PERIOD, JavaTS.CLASS);
        return new ClassLiteralTree(type);
    }

}
