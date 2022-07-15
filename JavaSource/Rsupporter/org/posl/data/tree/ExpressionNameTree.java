package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.Accessor;
import org.posl.data.resolution.ExpressionIdentifier;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node which is assumed as an expression name or method name contextually.
 * 
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.PROBABLY_OK)
public final class ExpressionNameTree implements ExpressionTree, Accessor, TypeTree{

    private final Accessor qualifier;
    private final IdentifierTree identifier;
    private ExpressionIdentifier resolution = null;

    public static final ExpressionNameTree EMPTY = new ExpressionNameTree();

    public ExpressionNameTree(Accessor qualifier, IdentifierTree identifier){
        this.qualifier = qualifier;
        this.identifier = identifier;
    }
    
    private ExpressionNameTree(){
        this(EMPTY, IdentifierTree.EMPTY);
    }

    static ExpressionNameTree parse(Accessor qualifier, JavaTokenManager src) throws ParsingException{
        return new ExpressionNameTree(qualifier, IdentifierTree.parse(src));
    }

    static ExpressionNameTree parse(JavaTokenManager src) throws ParsingException{
        var expr = new ExpressionNameTree(ExpressionNameTree.EMPTY, IdentifierTree.parse(src));
        while(src.match(JavaTS.PERIOD) && src.match(1, IDENTIFIERS)){
            src.skip(JavaTS.PERIOD);
            expr = parse(expr, src);
        }
        return expr;
    }

    static ExpressionTree convertToExpression(Tree t) throws ParsingException{
        if(t == EMPTY){
            return null;
        }else if(t instanceof ExpressionTree e){
            return e;
        }else if(t instanceof NameTree n){
            return new ExpressionNameTree(n.qualifier(), n.identifier());
        }else{
            throw new ParsingException(String.format("Tree \"%s\" cannot be converted to an expression.", t.toString()));
        }
    }

    public Accessor qualifier(){
        return qualifier;
    }

    public IdentifierTree identifier(){
        return identifier;
    }

    public boolean equals(Object o){
        if(o instanceof ExpressionNameTree n){
            return qualifier.equals(n.qualifier) && identifier.equals(n.identifier);
        }
        return false;
    }

    public String toString(){
        if(this == EMPTY){
            return "";
        }
        return ((qualifier == EMPTY)? "" : (qualifier.toString() + ".")) + identifier.toString();
    }

    public int hashCode(){
        if(this == EMPTY){
            return 0;
        }
        return ((qualifier == EMPTY)? 0 : qualifier.hashCode()) * 31 + identifier.hashCode();
    }

    @Override
    public ExpressionIdentifier resolution() throws ParsingException{
        return resolution;
    }

}
