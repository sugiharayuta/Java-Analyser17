package org.posl.data.tree;

/**
 * A tree node for an assignment expression.
 *
 * For example:
 * <pre>
 *   <em>variable</em> = <em>expression</em>
 * </pre>
 *
 * @jls 15.26.1 Simple Assignment Operator =
 *
 * @author me
 */
public record AssignmentTree(ExpressionTree variable, ExpressionTree expression) implements ExpressionTree{}