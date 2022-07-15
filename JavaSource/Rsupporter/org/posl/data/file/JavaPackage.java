package org.posl.data.file;

import java.util.Map;

import org.posl.data.resolution.Accessible;
import org.posl.data.tree.ExpressionNameTree;
import org.posl.data.tree.IdentifierTree;

public class JavaPackage implements Accessible{
    private final ExpressionNameTree name;
    private final Map<IdentifierTree, Accessible> contents;
    
    public JavaPackage(ExpressionNameTree name, Map<IdentifierTree, Accessible> contents){
        this.name = name;
        this.contents = contents;
    }
    
    public ExpressionNameTree qualifiedName(){
        return name;
    }

    public void accept(Map<IdentifierTree, Accessible> contents){
        this.contents.putAll(contents);
    }

    @Override
    public IdentifierTree simpleName(){
        return name.identifier();
    }

}
