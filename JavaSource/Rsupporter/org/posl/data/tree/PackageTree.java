package org.posl.data.tree;

import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.test.Tested;
import org.posl.test.Tested.Status;

/**
 * Represents the package declaration.
 *
 * @jls 7.3 Compilation Units
 * @jls 7.4 Package Declarations
 *
 * @author me
 */

@Tested(date = "2022/7/6", tester = "me", confidence = Status.CLEARLY_OK)
public record PackageTree(ArrayList<AnnotationTree> annotations, ExpressionNameTree name) implements Tree{

    static PackageTree parse(JavaTokenManager src) throws ParsingException{
        var annotations = Tree.resolveAnnotations(src);
        src.skip(JavaTS.PACKAGE);
        ExpressionNameTree name = ExpressionNameTree.parse(src);
        src.skip(JavaTS.SEMICOLON);
        return new PackageTree(annotations, name);
    }

    PackageTree(){
        this(new ArrayList<>(), new ExpressionNameTree(ExpressionNameTree.EMPTY, IdentifierTree.EMPTY));
    }

}