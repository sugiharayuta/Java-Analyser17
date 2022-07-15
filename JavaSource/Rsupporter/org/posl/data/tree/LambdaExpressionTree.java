package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a lambda expression.
 *
 * For example:
 * <pre>{@code
 *   ()->{}
 *   (List<String> ls)->ls.size()
 *   (x,y)-> { return x + y; }
 * }</pre>
 * 
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record LambdaExpressionTree(ArrayList<VariableTree> parameters, Tree body, BodyKind bodyKind) implements ExpressionTree{

    /**
     * Lambda expressions come in two forms:
     * <ul>
     * <li> expression lambdas, whose body is an expression, and
     * <li> statement lambdas, whose body is a block
     * </ul>
     */
    
    public enum BodyKind {
        /** enum constant for expression lambdas */
        EXPRESSION,
        /** enum constant for statement lambdas */
        STATEMENT
    }

    static LambdaExpressionTree parse(JavaTokenManager src) throws ParsingException{
        ArrayList<VariableTree> parameters = new ArrayList<>();
        if(src.match(JavaTS.LEFT_ROUND_BRACKET, JavaTS.RIGHT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET, JavaTS.RIGHT_ROUND_BRACKET);
        }else if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET);
            parameters.addAll(Tree.getList(LambdaExpressionTree::resolveLambdaParameter, JavaTS.RIGHT_ROUND_BRACKET, src));
            src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        }else{
            parameters.add(VariableTree.resolveImplicitlyTypedVariable(src));
        }
        src.skip(JavaTS.ARROW);
        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            return new LambdaExpressionTree(parameters, BlockTree.parse(src), BodyKind.STATEMENT);
        }
        return new LambdaExpressionTree(parameters, ExpressionTree.parse(src), BodyKind.EXPRESSION);
    }

    static VariableTree resolveLambdaParameter(JavaTokenManager src) throws ParsingException{
        if(IDENTIFIERS.contains(Tree.lookAhead(src, LookAheadMode.TYPE))){
            return VariableTree.resolveSingleDeclaration(src);
        }else{
            return VariableTree.resolveImplicitlyTypedVariable(src);
        }
    }

}
