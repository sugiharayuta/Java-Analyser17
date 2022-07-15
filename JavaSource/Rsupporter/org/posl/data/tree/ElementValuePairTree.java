package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;

public final class ElementValuePairTree implements ExpressionTree, Accessor{

    private final IdentifierTree identifier;
    private final ExpressionTree elementValue;

    private MethodTree resolution = null;

    public ElementValuePairTree(IdentifierTree identifier, ExpressionTree elementValue){
        this.identifier = identifier;
        this.elementValue = elementValue;
    }

    static ElementValuePairTree parse(JavaTokenManager src) throws ParsingException{
        IdentifierTree identifier;
        if(src.match(1, JavaTS.SIMPLE_ASSIGNMENT)){
            identifier = IdentifierTree.parse(src);
            src.skip(JavaTS.SIMPLE_ASSIGNMENT);
        }else{
            identifier = IdentifierTree.EMPTY;
        }
        return new ElementValuePairTree(identifier, resolveElementValue(src));
    }

    static ExpressionTree resolveElementValue(JavaTokenManager src) throws ParsingException{
        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            return NewArrayTree.parseArrayInitializer(ElementValuePairTree::resolveElementValue, src);
        }else if(src.match(JavaTS.AT_SIGN)){
            return AnnotationTree.parse(src);
        }else{
            return ExpressionTree.resolveConditionalExpression(src);
        }
    }

    public IdentifierTree identifier(){
        return identifier;
    }

    public ExpressionTree elementValue(){
        return elementValue;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof final ElementValuePairTree p){
            return identifier.equals(p.identifier) && elementValue.equals(p.elementValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ElementValurPairTree[identifier=%s, elementValue=%s]", identifier.toString(), elementValue.toString());
    }

    @Override
    public int hashCode() {
        return identifier.hashCode() * 31 + elementValue.hashCode();
    }

    @Override
    public MethodTree resolution() throws ParsingException{
        return resolution;
    }

}