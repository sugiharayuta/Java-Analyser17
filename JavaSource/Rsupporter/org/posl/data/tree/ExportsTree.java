package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an 'exports' directive in a module declaration.
 *
 * For example:
 * <pre>
 *    exports <em>package-name</em>;
 *    exports <em>package-name</em> to <em>module-name</em>;
 * </pre>
 *
 * @author me
 */
public record ExportsTree(ExpressionNameTree packageName, ArrayList<ExpressionNameTree> moduleNames) implements DirectiveTree{

    static ExportsTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.EXPORTS);
        var packageName = ExpressionNameTree.parse(src);

        ArrayList<ExpressionNameTree> moduleNames = new ArrayList<>();
        if(src.match(JavaTS.TO)){
            do{
                src.read();
                moduleNames.add(ExpressionNameTree.parse(src));
            }while(src.match(JavaTS.COMMA));
        }
        src.skip(JavaTS.SEMICOLON);
        return new ExportsTree(packageName, moduleNames);
    }

}
