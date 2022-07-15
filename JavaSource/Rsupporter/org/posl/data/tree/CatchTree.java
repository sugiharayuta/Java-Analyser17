package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a {@code catch} block in a {@code try} statement.
 *
 * For example:
 * <pre>
 *   catch ( <em>parameter</em> )
 *       <em>block</em>
 * </pre>
 *
 * @jls 14.20 The try statement
 *
 * @author me
 */
public record CatchTree(VariableTree parameter, BlockTree block) implements Tree{
    
    static CatchTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.CATCH, JavaTS.LEFT_ROUND_BRACKET);
        VariableTree parameter = VariableTree.resolveCatchFormalParameter(src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        return new CatchTree(parameter, BlockTree.parse(src));
    }

}
