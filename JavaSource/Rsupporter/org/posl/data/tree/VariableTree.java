package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.ExpressionIdentifier;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a variable declaration.
 *
 * For example:
 * <pre>
 *   <em>modifiers</em> <em>type</em> <em>name</em> <em>initializer</em> ;
 *   <em>modifiers</em> <em>type</em> <em>qualified-name</em>.this
 * </pre>
 *
 * @jls 8.3 Field Declarations
 * @jls 14.4 Local Variable Declaration Statements
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.MAYBE_OK)
public record VariableTree(ModifiersTree modifiers, TypeTree declaredType, TypeTree actualType, IdentifierTree name, 
                            IdentifierTree nameExpression, ExpressionTree initializer, VariableTree follows) implements StatementTree, ExpressionIdentifier{


    /**
     * Gets a field declaration or local variable declaration.
     */
    static VariableTree parse(JavaTokenManager src) throws ParsingException{
        return parse(ModifiersTree.parse(src), NameTree.resolveTypeOrName(src), src);
    }

    private static VariableTree parse(ModifiersTree modifiers, TypeTree declaredType, JavaTokenManager src) throws ParsingException{
        var name = IdentifierTree.parse(src);
        TypeTree actualType = NameTree.resolveDims(declaredType, src);
        ExpressionTree initializer = null;
        VariableTree follows = null;

        if(src.match(JavaTS.SIMPLE_ASSIGNMENT)){
            src.skip(JavaTS.SIMPLE_ASSIGNMENT);
            initializer = resolveVariableInitializer(src);
        }
        if(src.match(JavaTS.COMMA)){
            src.skip(JavaTS.COMMA);
            follows = VariableTree.parse(modifiers, declaredType, src);
        }
        return new VariableTree(modifiers, declaredType, actualType, name, null, initializer, follows);
    }

    static VariableTree resolveDeclarationStatement(JavaTokenManager src) throws ParsingException{
        var declaration = parse(src);
        src.skip(JavaTS.SEMICOLON);
        return declaration;
    }

    /**
     * Gets a single variable declaration which has no initializer.
     */
    static VariableTree resolveSingleDeclaration(JavaTokenManager src) throws ParsingException{
        var modifiers = ModifiersTree.parse(src);
        TypeTree declaredType = NameTree.resolveTypeOrName(src);
        TypeTree actualType;
        IdentifierTree name;
        if(Tree.lookAhead(src, LookAheadMode.ANNOTATIONS) == JavaTS.ELLIPSIS){
            actualType = VariableArityTypeTree.parse(declaredType, src);
            name = IdentifierTree.parse(src);   
        }else{
            if(src.match(JavaTS.THIS)){
                src.skip(JavaTS.THIS);
                return new VariableTree(modifiers, declaredType, declaredType, null, null, null, null);
            }else if(src.match(IDENTIFIERS) && src.match(1, JavaTS.PERIOD) && src.match(JavaTS.THIS)){
                IdentifierTree nameExpression = IdentifierTree.parse(src);
                src.skip(JavaTS.PERIOD, JavaTS.THIS);
                return new VariableTree(modifiers, declaredType, declaredType, null, nameExpression, null, null);
            }
            name = IdentifierTree.parse(src);
            actualType = NameTree.resolveDims(declaredType, src);
        }
        ExpressionTree initializer = null;
        if(src.match(JavaTS.SIMPLE_ASSIGNMENT)){
            src.skip(JavaTS.SIMPLE_ASSIGNMENT);
            initializer = resolveVariableInitializer(src);
        }
        return new VariableTree(modifiers, declaredType, actualType, name, null, initializer, null);
    }

    static VariableTree resolveImplicitlyTypedVariable(JavaTokenManager src) throws ParsingException{
        return new VariableTree(ModifiersTree.EMPTY, null, null, IdentifierTree.parse(src), null, null, null);
    }

    static VariableTree resolveCatchFormalParameter(JavaTokenManager src) throws ParsingException{
        var modifiers = ModifiersTree.parse(src);
        TypeTree type;
        if(Tree.lookAhead(src, LookAheadMode.TYPE) == JavaTS.VERTICAL_BAR){
            type = UnionTypeTree.parse(src);
        }else{
            type = NameTree.resolveTypeOrName(src);
        }
        return new VariableTree(modifiers, type, type, IdentifierTree.parse(src), null, null, null);
    }

    static ExpressionTree resolveVariableInitializer(JavaTokenManager src) throws ParsingException{
        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            return NewArrayTree.parseArrayInitializer(VariableTree::resolveVariableInitializer, src);
        }else{
            return ExpressionTree.parse(src);
        }
    }

    @Override
    public IdentifierTree simpleName(){
        return name;
    }

}
