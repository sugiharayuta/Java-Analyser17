package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an array type.
 *
 * For example:
 * <pre>
 *   <em>type</em> []
 * </pre>
 *
 * @jls 10.1 Array Types
 *
 * @author me
 */
public record ArrayTypeTree(TypeTree elementType) implements TypeTree{

    static ArrayTypeTree parse(TypeTree elementType, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET);
        return new ArrayTypeTree(elementType);
    }

}
