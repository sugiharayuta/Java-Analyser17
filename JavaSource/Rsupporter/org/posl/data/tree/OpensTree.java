package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an 'opens' directive in a module declaration.
 *
 * For example:
 * <pre>
 *    opens   <em>package-name</em>;
 *    opens   <em>package-name</em> to <em>module-name</em>;
 * </pre>
 *
 * @author me
 */
public record OpensTree(ExpressionNameTree packageName, ArrayList<ExpressionNameTree> moduleNames) implements DirectiveTree{

    static OpensTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.OPENS);
        var packageName = ExpressionNameTree.parse(src);

        ArrayList<ExpressionNameTree> moduleNames = new ArrayList<>();
        if(src.match(JavaTS.TO)){
            do{
                src.read();
                moduleNames.add(ExpressionNameTree.parse(src));
            }while(src.match(JavaTS.COMMA));
        }
        src.skip(JavaTS.SEMICOLON);
        return new OpensTree(packageName, moduleNames);
    }
    
}
