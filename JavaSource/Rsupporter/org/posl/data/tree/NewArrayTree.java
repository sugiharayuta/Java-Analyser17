package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;
import org.posl.util.functions.CEFunction;

/**
 * A tree node for an expression to create a new instance of an array.
 *
 * For example:
 * <pre>
 *   new <em>type</em> <em>dimensions</em> <em>initializers</em>
 *
 *   new <em>type</em> <em>dimensions</em> [ ] <em>initializers</em>
 * 
 *   { <em> expression </em>,  <em> expression </em>}
 * </pre>
 *
 * @jls 15.10.1 Array Creation Expressions
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record NewArrayTree(TypeTree type, ArrayList<ExpressionTree> dimensions, ArrayList<ExpressionTree> initializer)implements ExpressionTree{

    static NewArrayTree parse(TypeTree arrayElementType, JavaTokenManager src) throws ParsingException{
        TypeTree type = arrayElementType;
        ArrayList<AnnotationTree> annotations;
        ArrayList<ExpressionTree> dimensions = new ArrayList<>();
        while(followsDimExprs(src)){
            annotations = Tree.resolveAnnotations(src);
            src.skip(JavaTS.LEFT_SQUARE_BRACKET);
            dimensions.add(ExpressionTree.parse(src));
            type = new ArrayTypeTree(type);
            if(!annotations.isEmpty()){
                type = new AnnotatedTypeTree(annotations, type);
            }
            src.skip(JavaTS.RIGHT_SQUARE_BRACKET);
        }
        type = NameTree.resolveDims(type, src);
        
        ArrayList<ExpressionTree> initializer;
        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            initializer = parseArrayInitializer(VariableTree::resolveVariableInitializer, src).initializer;
        }else{
            initializer = new ArrayList<>();
        }
        return new NewArrayTree(type, dimensions, initializer);
    }

    static NewArrayTree parseArrayInitializer(CEFunction<JavaTokenManager, ExpressionTree> elementResolver, JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_CURLY_BRACKET);
        ArrayList<ExpressionTree> initializer = Tree.getList(elementResolver, JavaTS.RIGHT_CURLY_BRACKET, src);
        src.skip(JavaTS.RIGHT_CURLY_BRACKET);
        return new NewArrayTree(null, new ArrayList<>(), initializer);
    }

    static boolean followsDimExprs(JavaTokenManager src) throws ParsingException{
        var ptr = src.getPointer();
        return LookAheadMode.ANNOTATIONS.skip(ptr)
                && ptr.match(JavaTS.LEFT_SQUARE_BRACKET)
                && !ptr.match(1, JavaTS.RIGHT_SQUARE_BRACKET);
    }

}
