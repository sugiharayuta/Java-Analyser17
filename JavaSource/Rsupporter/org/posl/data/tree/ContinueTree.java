package org.posl.data.tree;

import org.posl.test.Tested;
import org.posl.test.Tested.Status;
import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;

/**
 * A tree node for a {@code continue} statement.
 *
 * For example:
 * <pre>
 *   continue;
 *   continue <em>label</em> ;
 * </pre>
 *
 * @jls 14.16 The continue Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.CLEARLY_OK)
public final class ContinueTree implements StatementTree, Accessor{

    private final IdentifierTree label;
    private LabeledStatementTree resolution = null;

    public ContinueTree(IdentifierTree label){
        this.label = label;
    }

    static ContinueTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.CONTINUE);
        IdentifierTree label = null;
        if(src.match(IDENTIFIERS)){
            label = IdentifierTree.parse(src);
        }
        src.skip(JavaTS.SEMICOLON);
        return new ContinueTree(label);
    }

    public IdentifierTree label(){
        return label;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final ContinueTree c){
            return label.equals(c.label);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return label.hashCode();
    }

    @Override
    public LabeledStatementTree resolution() throws ParsingException{
        return resolution;
    }

}
