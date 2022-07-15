package org.posl.data.tree;

import org.posl.data.resolution.Accessor;
import org.posl.data.resolution.ExpressionIdentifier;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * <p>
 * A tree node for a member access expression.
 * Note that preceding expression cannot be a conjuction of identifiers.
 * A conjuction of identifiers is classified as {@code ExpressionName}.
 * </p>
 * For example:
 * <pre>
 *   <em>expression</em> . <em>identifier</em>
 * </pre>
 *
 * @jls 6.5 Determining the Meaning of a Name
 * @jls 15.11 Field Access Expressions
 * @jls 15.12 Method Invocation Expressions
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public final class MemberSelectTree implements ExpressionTree, Accessor{

    private final ExpressionTree expression;
    private final IdentifierTree identifier;
    private ExpressionIdentifier resolution = null;

    public MemberSelectTree(ExpressionTree expression, IdentifierTree identifier){
        this.expression = expression;
        this.identifier = identifier;
    }

    static MemberSelectTree parse(ExpressionTree expression, JavaTokenManager src) throws ParsingException{
        return new MemberSelectTree(expression, IdentifierTree.parse(src));
    }

    public ExpressionTree expression(){
        return expression;
    }

    public IdentifierTree identifier(){
        return identifier;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof final MemberSelectTree m){
            return expression.equals(m.expression) && identifier.equals(m.identifier);
        }
        return false;
    }

    @Override
    public String toString(){
        return String.format("MemberSelectTree[expression=%s, identifier=%s]", expression.toString(), identifier.toString());
    }

    @Override
    public int hashCode(){
        return expression.hashCode() * 31 + identifier.hashCode();
    }

    @Override
    public ExpressionIdentifier resolution(){
        return resolution;
    }

}