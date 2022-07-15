package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a parenthesized pattern.
 *
 * For example:
 * <pre>
 *   ( <em>pattern</em> )
 * </pre>
 *
 * @jls 14.30.1 Kinds of Patterns
 *
 * @author me
 */

public record ParenthesizedPatternTree(PatternTree pattern) implements PatternTree{

    static ParenthesizedPatternTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.LEFT_ROUND_BRACKET);
        var pattern = PatternTree.parse(src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        return new ParenthesizedPatternTree(pattern);
    }

}
