package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a 'provides' directive in a module declaration.
 *
 * For example:
 * <pre>
 *    provides <em>service-name</em> with <em>implementation-name</em>;
 * </pre>

 * @author me
 */
public record ProvidesTree(ExpressionNameTree serviceName, ArrayList<ExpressionNameTree> implementionNames)implements DirectiveTree{

    static ProvidesTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.PROVIDES);
        var serviceName = ExpressionNameTree.parse(src);

        ArrayList<ExpressionNameTree> implementionNames = new ArrayList<>();
        src.skip(JavaTS.WITH);
        implementionNames.add(ExpressionNameTree.parse(src));
        while(src.match(JavaTS.COMMA)){
            src.skip(JavaTS.COMMA);
            implementionNames.add(ExpressionNameTree.parse(src));
        }
        src.skip(JavaTS.SEMICOLON);
        return new ProvidesTree(serviceName, implementionNames);
    }

}
