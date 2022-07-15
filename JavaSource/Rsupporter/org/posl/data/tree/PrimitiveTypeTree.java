package org.posl.data.tree;

import org.posl.data.resolution.PrimitiveType;

/**
 * A tree node for a primitive type.
 *
 * For example:
 * <pre>
 *   <em>primitiveTypeKind</em>
 * </pre>
 *
 * @jls 4.2 Primitive Types and Values
 * 
 * @author me
 */

public record PrimitiveTypeTree(PrimitiveType primitiveType) implements TypeTree{

    static PrimitiveTypeTree parse(JavaTokenManager src) throws ParsingException{
        return new PrimitiveTypeTree(PrimitiveType.get(src.read()));
    }

}
