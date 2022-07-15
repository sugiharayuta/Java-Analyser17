package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * A tree node for a {@code try} statement.
 *
 * For example:
 * <pre>
 *   try
 *       <em>block</em>
 *   <em>catches</em>
 *   finally
 *       <em>finallyBlock</em>
 * </pre>
 *
 * @jls 14.20 The try statement
 *
 * @author me
 */

@Tested(date = "2022/7/8", tester = "me", confidence = Status.MAYBE_OK)
public record TryTree(BlockTree block, ArrayList<CatchTree> catches, BlockTree finallyBlock, ArrayList<Tree> resources) implements StatementTree{

    static TryTree parse(JavaTokenManager src) throws ParsingException{
        src.skip(JavaTS.TRY);
        ArrayList<Tree> resources = new ArrayList<>();
        if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            src.skip(JavaTS.LEFT_ROUND_BRACKET);
            resources = Tree.getList(TryTree::resolveResource, JavaTS.SEMICOLON, JavaTS.RIGHT_ROUND_BRACKET, src);
            src.skip(JavaTS.RIGHT_ROUND_BRACKET);
        }
        var block = BlockTree.parse(src);
        ArrayList<CatchTree> catches = new ArrayList<>();
        while(src.match(JavaTS.CATCH)){
            catches.add(CatchTree.parse(src));
        }
        BlockTree finallyBlock = null;
        if(src.match(JavaTS.FINALLY)){
            src.skip(JavaTS.FINALLY);
            finallyBlock = BlockTree.parse(src);
        }
        if(catches.isEmpty() && finallyBlock == null && resources.isEmpty()){
            throw new ParsingException("Illegal try statement, it requires either catch clause, finally clause, and resource specification.");
        }
        return new TryTree(block, catches, finallyBlock, resources);
    }

    static Tree resolveResource(JavaTokenManager src) throws ParsingException{
        if(IDENTIFIERS.contains(Tree.lookAhead(src, LookAheadMode.MODIFIERS, LookAheadMode.TYPE))){
            return VariableTree.parse(src);
        }else{
            var access = ExpressionTree.resolvePrimary(src);
            if(access instanceof MemberSelectTree || access instanceof ExpressionNameTree){
                return access;
            }else{
                throw new ParsingException("Illegal resource in try-with-resources statement, expected variable access.");
            }
        }
    }

}
