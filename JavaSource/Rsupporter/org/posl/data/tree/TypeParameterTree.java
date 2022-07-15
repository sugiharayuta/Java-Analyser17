package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.TypeIdentifier;

/**
 * A tree node for a type parameter.
 *
 * For example:
 * <pre>
 *   <em>name</em>
 *
 *   <em>name</em> implements <em>bounds</em>
 *
 *   <em>annotations</em> <em>name</em>
 * </pre>
 *
 * @jls 4.4 Type Variables
 *
 * @author me
 * 
 */

public record TypeParameterTree(ArrayList<AnnotationTree> annotations, IdentifierTree name, ArrayList<TypeTree> bounds)implements TypeIdentifier{

    static TypeParameterTree parse(JavaTokenManager src) throws ParsingException{
        ArrayList<AnnotationTree> annotations = Tree.resolveAnnotations(src);
        IdentifierTree name = IdentifierTree.parse(src);
        ArrayList<TypeTree> bounds;
        if(src.match(JavaTS.EXTENDS)){
            src.skip(JavaTS.EXTENDS);
            bounds = Tree.getListWithoutBracket(NameTree::resolveNonArrayTypeOrName, JavaTS.AND, src);
        }else{
            bounds = new ArrayList<>();
        }
        return new TypeParameterTree(annotations, name, bounds);
    }

    @Override
    public IdentifierTree simpleName(){
        return name;
    }

}
