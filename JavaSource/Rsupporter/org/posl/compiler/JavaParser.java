package org.posl.compiler;

import java.io.File;

import org.posl.data.tree.CompilationUnitTree;
import org.posl.data.tree.JavaTokenManager;
import org.posl.data.tree.ParsingException;


public class JavaParser{
    
    public CompilationUnitTree parse(File file, TokenList l) throws ParsingException{
        JavaTokenManager src = new JavaTokenManager(l);
        try{
            return CompilationUnitTree.parse(file, src);
        }catch(ParsingException e){
            int i = 0;
            while(src.hasRest() && i < 10){
                System.out.print(src.read().text + " ");
                i++;
            }
            System.out.println();
            throw e;
        }
    }

}
