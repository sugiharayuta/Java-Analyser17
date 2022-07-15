package org.posl.data.tree;

import org.posl.data.resolution.ExpressionIdentifier;

/**
 * A tree node used as the base class for the different kinds of
 * patterns.
 *
 * @since 16
 */
public record TypePatternTree(ModifiersTree modifiers, TypeTree type, IdentifierTree identifier) implements PatternTree, ExpressionIdentifier{

    static TypePatternTree parse(JavaTokenManager src) throws ParsingException{
        var modifiers = ModifiersTree.parse(src);
        TypeTree type = NameTree.resolveTypeOrName(src);
        return new TypePatternTree(modifiers, type, IdentifierTree.parse(src));
    }

    @Override
    public IdentifierTree simpleName(){
        return identifier;
    }
    
}
