package org.posl.data.tree;

/**
 * A tree node for an "enhanced" {@code for} loop statement.
 *
 * For example:
 * <pre>
 *   for ( <em>variable</em> : <em>expression</em> )
 *       <em>statement</em>
 * </pre>
 *
 * @jls 14.14.2 The enhanced for statement
 *
 * @author me
 */
public record EnhancedForLoopTree(VariableTree variable, ExpressionTree expression, StatementTree statement) implements StatementTree{}