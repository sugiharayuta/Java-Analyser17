package org.posl.data.tree;

/**
 * A tree node for a type cast expression.
 *
 * For example:
 * <pre>
 *   ( <em>type</em> ) <em>expression</em>
 * </pre>
 *
 * @jls 15.16 Cast Expressions
 *
 * @author me
 */

public record TypeCastTree(Tree type, ExpressionTree expression) implements ExpressionTree{}
