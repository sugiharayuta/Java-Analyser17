package org.posl.data.tree;

/**
 * A tree node for an identifier expression.
 *
 * For example:
 * <pre>
 *   <em>name</em>
 * </pre>
 *
 * @jls 6.5.6.1 Simple Expression Names
 *
 * @author me
 */

public record IdentifierTree(String name)implements ExpressionTree{

    static final IdentifierTree EMPTY = new IdentifierTree("");

    static IdentifierTree parse(JavaTokenManager src) throws ParsingException{
        if(src.match(IDENTIFIERS)){
            return new IdentifierTree(src.read().text);
        }else{
            throw new IllegalTokenException(src.lookAhead(), "identifier");
        }
    }

    public String toString(){
        return name;
    }
    
}
