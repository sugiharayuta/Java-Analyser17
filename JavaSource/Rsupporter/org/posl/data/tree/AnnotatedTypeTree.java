package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.data.resolution.Accessible;
import org.posl.data.resolution.Accessor;

/**
 * A tree node for an annotated type.
 *
 * For example:
 * <pre>
 *    {@code @}<em>annotationType String</em>
 *    {@code @}<em>annotationType</em> ( <em>arguments</em> ) <em>Date</em>
 * </pre>
 *
 * @see "JSR 308: Annotations on Java Types"
 *
 * @author me
 */

public record AnnotatedTypeTree(ArrayList<AnnotationTree> annotations, TypeTree type) implements ExpressionTree, Accessor, TypeTree{

    @Override
    public Accessible resolution() throws ParsingException{
        if(type instanceof Accessor a){
            return a.resolution();
        }else{
            throw new ParsingException(String.format("\"%s\" is not a reference type."));
        }
    }       
    
}
