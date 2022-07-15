package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.ExpressionIdentifier;

/**
 * WARNING! Record Patterns is a preview feature in Java19.
 * This parser parses record patterns as valid, but current Java may not.
 * 
 * For example:
 * <pre>
 *   <em>type</em> <em>identifier</em> < <em>typeArguments</em> >(<em>type</em> <em>identifier</em>, <em>type</em> <em>identifier</em>) <em>identifier</em>
 * </pre>
 * 
 * 
 * @author me
 */

public record RecordPatternTree(ModifiersTree modifiers, Tree type, ArrayList<PatternTree> componentPatterns, IdentifierTree identifier) implements PatternTree, ExpressionIdentifier{
    
    static RecordPatternTree parse(JavaTokenManager src) throws ParsingException{
        var modifiers = ModifiersTree.parse(src);
        Tree type = NameTree.resolveTypeOrName(src);
        src.skip(JavaTS.LEFT_ROUND_BRACKET);
        ArrayList<PatternTree> componentPatterns = Tree.getList(PatternTree::parse, JavaTS.RIGHT_ROUND_BRACKET, src);
        src.skip(JavaTS.RIGHT_ROUND_BRACKET);

        IdentifierTree identifier;
        if(src.match(IDENTIFIERS) && !src.match(JavaTS.WHEN)){
            identifier = IdentifierTree.parse(src);
        }else{
            identifier = null;
        }
        return new RecordPatternTree(modifiers, type, componentPatterns, identifier);
    }

    @Override
    public IdentifierTree simpleName(){
        return identifier;
    }

}
