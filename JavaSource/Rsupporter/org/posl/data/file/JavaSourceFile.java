package org.posl.data.file;

import java.io.File;

import org.posl.data.resolution.Accessible;
import org.posl.data.tree.ExpressionNameTree;
import org.posl.data.tree.IdentifierTree;

public class JavaSourceFile implements Accessible{

    final ExpressionNameTree name;
    final File file;

    public JavaSourceFile(ExpressionNameTree name, File file){
        this.name = name;
        this.file = file;
        
    }

    @Override
    public IdentifierTree simpleName(){
        return name.identifier();
    }
    
}
