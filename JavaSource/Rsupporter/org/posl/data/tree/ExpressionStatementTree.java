package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.UnaryTree.UnaryOperator;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for an expression statement.
 *
 * For example:
 * <pre>
 *   <em>expression</em> ;
 * </pre>
 *
 * @jls 14.8 Expression Statements
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record ExpressionStatementTree(ExpressionTree expression) implements StatementTree{

    static ExpressionStatementTree parse(JavaTokenManager src) throws ParsingException{
        ExpressionStatementTree statementExpression = resolveStatementExpression(src);
        src.skip(JavaTS.SEMICOLON);
        return statementExpression;
    }

    static ExpressionStatementTree resolveStatementExpression(JavaTokenManager src) throws ParsingException{
        ExpressionTree expr = ExpressionTree.parse(src);
        if(expr instanceof AssignmentTree
            || expr instanceof CompoundAssignmentTree
            || expr instanceof MethodInvocationTree
            || expr instanceof NewClassTree
            || (expr instanceof UnaryTree u
                && (u.operatorType() == UnaryOperator.PREFIX || u.operatorType() == UnaryOperator.POSTFIX)
                && (u.operatorToken() == JavaTS.INCREMENT || u.operatorToken() == JavaTS.DECREMENT))){
            return new ExpressionStatementTree(expr);
        }else{
            throw new ParsingException("Illegal statement expression, expected increment/decrement expression, method invocation, class instance creation expression and assignment.");
        }
    }

}
