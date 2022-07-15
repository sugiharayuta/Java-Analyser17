package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an {@code instanceof} expression.
 *
 * For example:
 * <pre>
 *   <em>expression</em> instanceof <em>type</em>
 * </pre>
 *
 * @jls 15.20.2 Type Comparison Operator instanceof
 *
 * @author me
 */

public record InstanceOfTree(ExpressionTree expression, Tree type, PatternTree pattern) implements ExpressionTree{

    static InstanceOfTree parse(ExpressionTree expression, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.INSTANCEOF);
        if(PatternTree.followsPattern(src)){
            return new InstanceOfTree(expression, null, PatternTree.parse(src));
        }
        return new InstanceOfTree(expression, NameTree.resolveTypeOrName(src), null);
    }

}

