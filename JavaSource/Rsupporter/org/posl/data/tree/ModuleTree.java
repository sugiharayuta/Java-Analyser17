package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;

/**
 * A tree node for a module declaration.
 *
 * For example:
 * <pre>
 *    <em>annotations</em>
 *    [open] module <em>module-name</em> {
 *        <em>directives</em>
 *    }
 * </pre>
 *
 * @since 9
 */
public record ModuleTree(ArrayList<AnnotationTree> annotations, ModuleKind moduleType, ExpressionNameTree name, ArrayList<DirectiveTree> directives) implements Tree{

    /**
     * The kind of the module.
     */
    enum ModuleKind {
        /**
         * Open module.
         */
        OPEN,
        /**
         * Strong module.
         */
        STRONG;
    }

    static ModuleTree parse(JavaTokenManager src) throws ParsingException{
        var annotations = Tree.resolveAnnotations(src);
        ModuleKind moduleType;
        if(src.match(JavaTS.OPEN)){
            src.skip(JavaTS.OPEN);
            moduleType = ModuleKind.OPEN;
        }else{
            moduleType = ModuleKind.STRONG;
        }
        src.skip(JavaTS.MODULE);
        ExpressionNameTree name = ExpressionNameTree.parse(src);

        ArrayList<DirectiveTree> directives = new ArrayList<>();
        src.skip(JavaTS.LEFT_CURLY_BRACKET);
        while(!src.match(JavaTS.RIGHT_CURLY_BRACKET)){
            directives.add(switch(src.lookAhead().resolution){
                case REQUIRES -> RequiresTree.parse(src);
                case EXPORTS -> ExportsTree.parse(src);
                case OPENS -> OpensTree.parse(src);
                case USES -> UsesTree.parse(src);
                case PROVIDES -> ProvidesTree.parse(src);
                default -> {throw new IllegalTokenException(src.lookAhead(), "module directive");}
            });
        }
        src.skip(JavaTS.RIGHT_CURLY_BRACKET);
        return new ModuleTree(annotations, moduleType, name, directives);
    }

}
