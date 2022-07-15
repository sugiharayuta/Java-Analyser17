package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code switch} statement.
 *
 * For example:
 * <pre>
 *   switch ( <em>expression</em> ) {
 *     <em>cases</em>
 *   }
 * </pre>
 *
 * @jls 14.11 The switch Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.MAYBE_OK)
public record SwitchTree(ExpressionTree expression, ArrayList<CaseTree> cases) implements StatementTree{

    static SwitchTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.SWITCH, JavaTS.LEFT_ROUND_BRACKET);
        var expression = ExpressionTree.parse(src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET, JavaTS.LEFT_CURLY_BRACKET);

        ArrayList<CaseTree> cases = new ArrayList<>();
        while(!src.match(JavaTS.RIGHT_CURLY_BRACKET)){
            cases.add(CaseTree.parse(src));
        }
        src.skip(JavaTS.RIGHT_CURLY_BRACKET);
        return new SwitchTree(expression, cases);
    }

}
