package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for an import declaration.
 *
 * For example:
 * <pre>
 *   import <em>qualifiedIdentifier</em> ;
 *
 *   static import <em>qualifiedIdentifier</em> ;
 * </pre>
 *
 * @jls 7.5 Import Declarations
 *
 * @author me
 */

@Tested(date = "2022/7/6", tester = "me", confidence = Status.CLEARLY_OK)
public record ImportTree(boolean isStatic, boolean isOnDemand, ExpressionNameTree qualifiedName) implements Tree{

    static ImportTree parse(JavaTokenManager src) throws ParsingException{
        boolean isStatic;
        boolean isOnDemand;

        src.skip(JavaTS.IMPORT);
        if(isStatic = src.match(JavaTS.STATIC)){
            src.skip(JavaTS.STATIC);
        }
        
        var qualifiedName = ExpressionNameTree.parse(src);
        if(isOnDemand = src.match(JavaTS.PERIOD, JavaTS.ASTERISK)){
            src.skip(JavaTS.PERIOD, JavaTS.ASTERISK);
        }
        src.skip(JavaTS.SEMICOLON);

        return new ImportTree(isStatic, isOnDemand, qualifiedName);
    }

}
