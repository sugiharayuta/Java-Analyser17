package org.posl.data.tree;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for compound assignment operator.
 *
 * For example:
 * <pre>
 *   <em>variable</em> <em>operator</em> <em>expression</em>
 * </pre>
 *
 * @jls 15.26.2 Compound Assignment Operators
 *
 * @author me
 */

public record CompoundAssignmentTree(ExpressionTree variable, JavaTS operator, ExpressionTree expression) implements ExpressionTree{}
