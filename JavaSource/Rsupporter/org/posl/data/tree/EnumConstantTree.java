package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.resolution.ExpressionIdentifier;

public record EnumConstantTree(ArrayList<AnnotationTree> annotations, IdentifierTree name, ArrayList<ExpressionTree> arguments, ArrayList<Tree> members) implements Tree, ExpressionIdentifier{

    static EnumConstantTree parse(JavaTokenManager src) throws ParsingException{
        var annotations = Tree.resolveAnnotations(src);
        IdentifierTree name = IdentifierTree.parse(src);
        ArrayList<ExpressionTree> arguments = null;
        if(src.match(JavaTS.LEFT_ROUND_BRACKET)){
            arguments = ExpressionTree.resolveArguments(src);
        }
        ArrayList<Tree> members = null;
        if(src.match(JavaTS.LEFT_CURLY_BRACKET)){
            src.skip(JavaTS.LEFT_CURLY_BRACKET);
            members = new ArrayList<>();
            while(!src.match(JavaTS.RIGHT_CURLY_BRACKET)){
                members.add(ClassTree.resolveMember(DeclarationType.CLASS, src));
            }
            src.skip(JavaTS.RIGHT_CURLY_BRACKET);
        }
        return new EnumConstantTree(annotations, name, arguments, members);
    }

    @Override
    public IdentifierTree simpleName(){
        return name;
    }

}
