package org.posl.compiler;

import java.io.File;

import org.posl.data.tree.CompilationUnitTree;
import org.posl.data.tree.ParsingException;

public class JavaCompiler{

    private final JavaLexer lexer = new JavaLexer();
    private final JavaParser parser = new JavaParser();

    public CompilationUnitTree compile(File file){
        try{
            return parser.parse(file, lexer.run(file));
        }catch(ParsingException e){
            System.out.println(e);
            e.printStackTrace();
            return CompilationUnitTree.ERROR;
        }
    }
}
