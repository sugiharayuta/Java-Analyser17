package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a wildcard type argument.
 * Use {@link #getKind getKind} to determine the kind of bound.
 *
 * For example:
 * <pre>
 *   ?
 *
 *   ? implements <em>bound</em>
 *
 *   ? super <em>bound</em>
 * </pre>
 *
 * @jls 4.5.1 Type Arguments of Parameterized Types
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record WildcardTree(ArrayList<AnnotationTree> annotations, WildcardType wildcardType, TypeTree bound) implements TypeTree{

    enum WildcardType{
        WILD,
        CONVARIANT,
        CONTRAVARIANT;
    }

    static WildcardTree parse(JavaTokenManager src) throws ParsingException{
        ArrayList<AnnotationTree> annotations = Tree.resolveAnnotations(src); 
        src.skip(JavaTS.QUESTION);
        if(src.match(JavaTS.EXTENDS)){
            src.skip(JavaTS.EXTENDS);
            return new WildcardTree(annotations, WildcardType.CONVARIANT, NameTree.resolveTypeOrName(src));
        }else if(src.match(JavaTS.SUPER)){
            src.skip(JavaTS.SUPER);
            return new WildcardTree(annotations, WildcardType.CONTRAVARIANT, NameTree.resolveTypeOrName(src));
        }else{
            return new WildcardTree(annotations, WildcardType.WILD, null);
        }
    }

}
