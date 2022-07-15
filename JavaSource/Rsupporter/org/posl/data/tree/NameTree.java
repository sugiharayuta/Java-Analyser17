package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessible;
import org.posl.data.resolution.Accessor;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a type, expression, or package name.
 *
 * For example:
 * <pre>
 * 
 *    <em>identifier</em> . <em>identifier</em>
 * 
 * </pre>
 * 
 * @author me
 * 
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public final class NameTree implements Accessor, TypeTree{

    private final Accessor qualifier;
    private final IdentifierTree identifier;
    private Accessible resolution = null;

    public NameTree(Accessor qualifier, IdentifierTree identifier){
        this.qualifier = qualifier;
        this.identifier = identifier;
    }
    
    private static NameTree parse(Accessor qualifier, JavaTokenManager src) throws ParsingException{
        return new NameTree(qualifier, IdentifierTree.parse(src));
    }

    static TypeTree resolveTypeOrName(JavaTokenManager src) throws ParsingException{
        TypeTree type = resolveNonArrayTypeOrName(src);
        return resolveDims(type, src);
    }

    static TypeTree resolveNonArrayTypeOrName(JavaTokenManager src) throws ParsingException{
        TypeTree type = ExpressionNameTree.EMPTY;
        while(true){
            type = resolveSimpleNameType(type, src);
            if(src.match(JavaTS.PERIOD) && (src.match(1, IDENTIFIERS) || src.match(1, JavaTS.AT_SIGN))){
                src.skip(JavaTS.PERIOD);
            }else{
                break;
            }
        }
        return type;
    }

    static TypeTree resolveSimpleNameType(TypeTree type, JavaTokenManager src) throws ParsingException{
        var annotations = Tree.resolveAnnotations(src);
        if(src.match(JavaTS.VAR)){
            type = VarTree.parse(src);
        }else if(src.match(IDENTIFIERS)){
            if(type instanceof Accessor a){
                type = parse(a, src);
                if(src.match(JavaTS.LESS_THAN)){
                    type = new ParameterizedTypeTree(a, Tree.resolveTypeArguments(src));
                }
            }else{
                throw new ParsingException(String.format("\"%s\" cannot be a qualifier.", type.toString()));
            }
        }else{
            if(type != ExpressionNameTree.EMPTY){
                throw new IllegalTokenException(src.lookAhead(), "reference type name");
            }
            if(src.match(PRIMITIVE_TYPES)){
                type = PrimitiveTypeTree.parse(src);
            }else if(src.match(JavaTS.VOID)){
                type = VoidTree.parse(src);
            }else{
                throw new IllegalTokenException(src.lookAhead(), "type name");
            }
        }
        if(!annotations.isEmpty()){
            return new AnnotatedTypeTree(annotations, type);
        }
        return type;
    }


    static TypeTree resolveDims(TypeTree type, JavaTokenManager src) throws ParsingException{
        ArrayList<AnnotationTree> annotations;
        while(followsDims(src)){
            annotations = Tree.resolveAnnotations(src);
            if(src.match(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET)){
                type = ArrayTypeTree.parse(type, src);
                if(!annotations.isEmpty()){
                    type = new AnnotatedTypeTree(annotations, type);
                }
            }
        }
        return type;
    }

    static boolean followsDims(JavaTokenManager src) throws ParsingException{
        var ptr = src.getPointer();
        return LookAheadMode.ANNOTATIONS.skip(ptr)
                && ptr.match(JavaTS.LEFT_SQUARE_BRACKET, JavaTS.RIGHT_SQUARE_BRACKET);
        
    }

    public Accessor qualifier(){
        return qualifier;
    }

    public IdentifierTree identifier(){
        return identifier;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final NameTree n){
            return qualifier.equals(n.qualifier) && identifier.equals(n.identifier);
        }
        return false;
    }

    @Override
    public String toString(){
        return qualifier.toString() + "." + identifier.toString();
    }

    @Override
    public int hashCode(){
        return qualifier.hashCode() * 31 + identifier.hashCode();
    }

    @Override
    public Accessible resolution(){
        return resolution;
    }
}

