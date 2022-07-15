package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.data.resolution.Accessor;

/**
 * A tree node for a type expression involving type parameters.
 *
 * For example:
 * <pre>
 *   <em>type</em> &lt; <em>typeArguments</em> &gt;
 * </pre>
 *
 * @jls 4.5.1 Type Arguments of Parameterized Types
 *
 * @author me
 * 
 */
public record ParameterizedTypeTree(Accessor type, ArrayList<TypeTree> typeArguments) implements Accessor, TypeTree{
    
    static final ArrayList<TypeTree> DIAMOND = new ArrayList<>();

    @Override
    public ClassTree resolution() throws ParsingException{
        if(type.resolution() instanceof ClassTree c){
            return c;
        }else{
            throw new ParsingException(String.format("Type \"%s\" cannot be parameterized.", type.toString()));
        }
        
    }

}
