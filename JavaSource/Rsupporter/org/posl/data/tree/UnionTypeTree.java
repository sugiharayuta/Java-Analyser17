package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a union type expression in a multicatch
 * variable declaration.
 *
 * @author Maurizio Cimadamore
 *
 * @since 1.7
 */
public record UnionTypeTree(ArrayList<TypeTree> typeAlternatives) implements TypeTree{

    static UnionTypeTree parse(JavaTokenManager src) throws ParsingException{
        return new UnionTypeTree(Tree.getListWithoutBracket(NameTree::resolveTypeOrName, JavaTS.VERTICAL_BAR, src));
    }

}
