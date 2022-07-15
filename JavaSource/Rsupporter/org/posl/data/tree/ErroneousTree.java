package org.posl.data.tree;

import java.util.ArrayList;

/**
 * A tree node to stand in for a malformed expression.
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */


@Deprecated
public record ErroneousTree(ArrayList<Tree> errorTrees)implements ExpressionTree{}
