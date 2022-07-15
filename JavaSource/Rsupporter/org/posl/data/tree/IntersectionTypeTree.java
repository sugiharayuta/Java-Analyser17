package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an intersection type in a cast expression.
 *
 * @author me
 */

public record IntersectionTypeTree(ArrayList<TypeTree> bounds) implements TypeTree{

    static IntersectionTypeTree parse(JavaTokenManager src) throws ParsingException{
        return new IntersectionTypeTree(Tree.getListWithoutBracket(NameTree::resolveTypeOrName, JavaTS.AND, src));
    }

}
