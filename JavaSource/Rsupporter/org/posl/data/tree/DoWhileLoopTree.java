package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code do} statement.
 *
 * For example:
 * <pre>
 *   do
 *       <em>statement</em>
 *   while ( <em>expression</em> );
 * </pre>
 *
 * @jls 14.13 The do Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record DoWhileLoopTree(ExpressionTree condition, StatementTree statement) implements StatementTree{

    static DoWhileLoopTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.DO);
        var statement = StatementTree.parse(src);
        src.skip(JavaTS.WHILE, JavaTS.LEFT_ROUND_BRACKET);
        var condition = ExpressionTree.parse(src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET, JavaTS.SEMICOLON);
        return new DoWhileLoopTree(condition, statement);
    }

}