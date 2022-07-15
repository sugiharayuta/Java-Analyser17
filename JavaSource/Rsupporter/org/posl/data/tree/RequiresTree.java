package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a 'requires' directive in a module declaration.
 *
 * For example:
 * <pre>
 *    requires <em>module-name</em>;
 *    requires static <em>module-name</em>;
 *    requires transitive <em>module-name</em>;
 * </pre>
 *
 * @since 9
 */
public record RequiresTree(boolean isStatic, boolean isTransitive, ExpressionNameTree moduleName) implements DirectiveTree {

    static RequiresTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.REQUIRES);
        boolean isTransitive = false;
        boolean isStatic = false;
        LOOP: while(true){
            switch(src.lookAhead().resolution){
                case TRANSITIVE -> isTransitive = true;
                case STATIC -> isStatic = true;
                default -> {break LOOP;}
            }
            src.read();
        }
        var moduleName = ExpressionNameTree.parse(src);
        src.skip(JavaTS.SEMICOLON);
        return new RequiresTree(isStatic, isTransitive, moduleName);
    }

}
