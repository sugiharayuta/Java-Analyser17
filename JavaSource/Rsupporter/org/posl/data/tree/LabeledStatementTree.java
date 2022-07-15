package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessible;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a labeled statement.
 *
 * For example:
 * <pre>
 *   <em>label</em> : <em>statement</em>
 * </pre>
 *
 * @jls 14.7 Labeled Statements
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public record LabeledStatementTree(IdentifierTree label, StatementTree statement) implements StatementTree, Accessible{

    static LabeledStatementTree parse(JavaTokenManager src) throws ParsingException{
        var label = IdentifierTree.parse(src);
        src.skip(JavaTS.COLON);
        return new LabeledStatementTree(label, StatementTree.parse(src));
    }

    @Override
    public IdentifierTree simpleName(){
        return label;
    }
}
