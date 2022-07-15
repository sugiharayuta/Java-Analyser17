package org.posl.data.tree;

import java.util.ArrayList;

/**
 * A tree node for a basic {@code for} loop statement.
 *
 * For example:
 * <pre>
 *   for ( <em>initializer</em> ; <em>condition</em> ; <em>update</em> )
 *       <em>statement</em>
 * </pre>
 *
 * @jls 14.14.1 The basic for Statement
 *
 * @author me
 */
public record ForLoopTree(ArrayList<StatementTree> initializer, ExpressionTree condition, ArrayList<ExpressionStatementTree> update, StatementTree statement) implements StatementTree{}
