package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a 'uses' directive in a module declaration.
 *
 * For example:
 * <pre>
 *    uses <em>service-name</em>;
 * </pre>
 *
 * @author me
 */
public record UsesTree(ExpressionNameTree serviceName)implements DirectiveTree{

    static UsesTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.USES);
        var serviceName = ExpressionNameTree.parse(src);
        src.skip(JavaTS.SEMICOLON);
        return new UsesTree(serviceName);
    }

}
