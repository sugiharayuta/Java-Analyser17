package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code break} statement.
 *
 * For example:
 * <pre>
 *   break;
 *
 *   break <em>label</em> ;
 * </pre>
 *
 * @jls 14.15 The break Statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.CLEARLY_OK) 
public final class BreakTree implements StatementTree, Accessor{

    private final IdentifierTree label;
    private LabeledStatementTree resolution = null;

    public BreakTree(IdentifierTree label){
        this.label = label;
    }

    static BreakTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.BREAK);
        IdentifierTree label = null;
        if(src.match(IDENTIFIERS)){
            label = IdentifierTree.parse(src);
        }
        src.skip(JavaTS.SEMICOLON);
        return new BreakTree(label);
    }

    public IdentifierTree label(){
        return label;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final BreakTree b){
            return label.equals(b.label);
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
