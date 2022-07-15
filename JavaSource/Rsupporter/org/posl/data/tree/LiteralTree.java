package org.posl.data.tree;

import org.posl.data.tokens.Literal;
import org.posl.data.tokens.Token;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a literal expression.
 * Use {@link #getKind getKind} to determine the kind of literal.
 *
 * For example:
 * <pre>
 *   <em>value</em>
 * </pre>
 *
 * @jls 15.28 Constant Expressions
 *
 * @author me
 * 
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record LiteralTree(String value) implements ExpressionTree{
    
    static LiteralTree parse(JavaTokenManager src) throws ParsingException{
        Token t = src.read();
        if(t instanceof Literal l){
            return new LiteralTree(l.text);
        }
        throw new IllegalTokenException(t, "literal");
    }

}
