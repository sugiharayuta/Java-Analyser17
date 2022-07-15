package org.posl.data.tree;

import java.io.File;
import java.util.ArrayList;

import org.posl.compiler.syntax.javalang.JavaTS;
import org.posl.data.tree.ModuleTree.ModuleKind;

/**
 * Represents the abstract syntax tree for ordinary compilation units
 * and modular compilation units.
 *
 * @jls 7.3 Compilation Units
 * @jls 7.4 Package Declarations
 * @jls 7.7 Module Declarations
 *
 * @author me
 */
public record CompilationUnitTree(ModuleTree module, PackageTree packageDecl, ArrayList<ImportTree> imports,  ArrayList<Tree> typeDecls, File sourceFile) implements Tree{

    public static final CompilationUnitTree ERROR = new CompilationUnitTree(null, null, new ArrayList<>(0), new ArrayList<>(0), null);
    public static final CompilationUnitTree UNNAMED_MODULE = new CompilationUnitTree(
        new ModuleTree(new ArrayList<>(0), ModuleKind.OPEN, ExpressionNameTree.EMPTY, new ArrayList<>(0)), null, new ArrayList<>(0), new ArrayList<>(0), null);
    
    public static CompilationUnitTree parse(File sourceFile, JavaTokenManager src) throws ParsingException{
        JavaTS lookAfterAnnotations = Tree.lookAhead(src, LookAheadMode.ANNOTATIONS);
        PackageTree packageDecl;

        if(lookAfterAnnotations == JavaTS.PACKAGE){
            //If there is a package declaration, get it.
            packageDecl = PackageTree.parse(src);
        }else{
            //If there is no package declaration, set current package as a no-name-package.
            packageDecl = new PackageTree();
        }

        ArrayList<ImportTree> imports = new ArrayList<>();
        while(src.match(JavaTS.IMPORT)){
            imports.add(ImportTree.parse(src));
        }

        ModuleTree module = null;
        ArrayList<Tree> typeDecls = new ArrayList<>();

        lookAfterAnnotations = Tree.lookAhead(src, LookAheadMode.ANNOTATIONS);
        if(lookAfterAnnotations == JavaTS.OPEN || lookAfterAnnotations == JavaTS.MODULE){
            module = ModuleTree.parse(src);
        }else{
            while(src.hasRest()){
                if(src.match(JavaTS.SEMICOLON)){
                    typeDecls.add(EmptyStatementTree.parse(src));
                }else{
                    var declType = DeclarationType.lookAheadDeclType(src);
                    typeDecls.add(ClassTree.parse(declType, src));
                    
                }
            }
        }

        return new CompilationUnitTree(module, packageDecl, imports, typeDecls, sourceFile);
    }

}
