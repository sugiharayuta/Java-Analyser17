package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for an empty (skip) statement.
 *
 * For example:
 * <pre>
 *    ;
 * </pre>
 *
 * @jls 14.6 The Empty Statement
 *
 * @author me
 */

public record EmptyStatementTree() implements StatementTree{

    static EmptyStatementTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.SEMICOLON);
        return new EmptyStatementTree();
    }

}
