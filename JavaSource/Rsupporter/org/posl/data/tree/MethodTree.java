package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessible;

/**
 * A tree node for a method or annotation type element declaration.
 *
 * For example:
 * <pre>
 *   <em>modifiers</em> <em>typeParameters</em> <em>type</em> <em>name</em>
 *      ( <em>parameters</em> )
 *      <em>body</em>
 *
 *   <em>modifiers</em> <em>type</em> <em>name</em> () default <em>defaultValue</em>
 * </pre>
 *
 * @jls 8.4 Method Declarations
 * @jls 8.6 Instance Initializers
 * @jls 8.7 Static Initializers
 * @jls 9.4 Method Declarations
 * @jls 9.6.1 Annotation Type Elements
 *
 * @author me
 */

public record MethodTree(ModifiersTree modifiers, ArrayList<TypeParameterTree> typeParameters,
                            TypeTree declaredType, TypeTree actualType, IdentifierTree name, VariableTree receiverParameter,
                            ArrayList<VariableTree> parameters, ArrayList<TypeTree> exceptions, BlockTree body, Tree defaultValue) implements Tree, Accessible{

    /**
     * Parses method declaration.
     * @throws ParsingException
     */

    static MethodTree parse(JavaTokenManager src) throws ParsingException{
        var modifiers = ModifiersTree.parse(src);
        var typeParameters = new ArrayList<TypeParameterTree>();
        if(src.match(JavaTS.LESS_THAN)){
            src.skip(JavaTS.LESS_THAN);
            typeParameters = Tree.getList(TypeParameterTree::parse, JavaTS.GREATER_THAN, src);
            src.skip(JavaTS.GREATER_THAN);
        }

        TypeTree declaredType;
        TypeTree actualType;
        if(src.match(1, JavaTS.LEFT_ROUND_BRACKET) || src.match(1, JavaTS.LEFT_CURLY_BRACKET)){
            actualType = declaredType = null;
        }else{
            actualType = declaredType =  NameTree.resolveTypeOrName(src);
        }
        var name = IdentifierTree.parse(src);

        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            return new MethodTree(modifiers, typeParameters, null, null, name, null, null, new ArrayList<>(), BlockTree.parse(src), null);
        }

        var parameters = new ArrayList<VariableTree>();
        var receiverParameter = src.match(JavaTS.LEFT_CURLY_BRACKET)? null : resolveParameters(parameters, src);
        if(src.match(JavaTS.AT_SIGN) || src.match(JavaTS.LEFT_SQUARE_BRACKET)){
            actualType = NameTree.resolveDims(declaredType, src);
        }

        var exceptions = new ArrayList<TypeTree>();
        if(src.match(JavaTS.THROWS)){
            src.skip(JavaTS.THROWS);
            exceptions.addAll(Tree.getListWithoutBracket(NameTree::resolveNonArrayTypeOrName, JavaTS.COMMA, src));
        }

        BlockTree body = null;
        Tree defaultValue = null;
        if(src.match(JavaTS.DEFAULT)){
            src.skip(JavaTS.DEFAULT);
            defaultValue = ElementValuePairTree.resolveElementValue(src);
            src.skip(JavaTS.SEMICOLON);
        }else if(src.match(JavaTS.SEMICOLON)){
            src.skip(JavaTS.SEMICOLON);
        }else if(src.match()){
            body = BlockTree.parse(src);
        }else{
            throw new IllegalTokenException(src.lookAhead(), "method body");
        }
        return new MethodTree(modifiers, typeParameters, declaredType, actualType, name, receiverParameter, parameters, exceptions, body, defaultValue);
    }

    private static VariableTree resolveParameters(ArrayList<VariableTree> parameters, JavaTokenManager src) throws ParsingException{
        VariableTree receiverParameter = null;

        if(src.match(JavaTS.LEFT_ROUND_BRACKET, JavaTS.RIGHT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET, JavaTS.RIGHT_ROUND_BRACKET);
        }else if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET);
            for(int i = 0;;i++, src.skip(JavaTS.COMMA)){
                var parameter = VariableTree.resolveSingleDeclaration(src);
                if(parameter.name() == null){
                    if(i == 0){
                        receiverParameter = parameter;
                    }else{
                        throw new ParsingException("A receiver parameter must come the first in method parameters.");
                    }
                }else{
                    parameters.add(parameter);
                }
                if(src.match(JavaTS.RIGHT_ROUND_BRACKET)){
                    break;
                }
            }
            src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        }else{
            throw new IllegalTokenException(src.lookAhead(), "method parameters");
        }
        return receiverParameter;
    }

    @Override
    public IdentifierTree simpleName(){
        return name;
    }

}
