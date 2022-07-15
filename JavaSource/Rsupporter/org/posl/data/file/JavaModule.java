package org.posl.data.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.posl.data.resolution.Accessible;
import org.posl.data.tree.CompilationUnitTree;
import org.posl.data.tree.ExpressionNameTree;
import org.posl.data.tree.IdentifierTree;

public class JavaModule implements Accessible{

    private final ExpressionNameTree name;
    private final CompilationUnitTree specification;
    private final Map<ExpressionNameTree, JavaPackage> associated = new ConcurrentHashMap<>();

    public JavaModule(CompilationUnitTree specification){
        this.specification = specification;
        this.name = specification.module().name();
    }

    public void associatePackage(JavaPackage p){
        associated.put(p.qualifiedName(), p);
    }

    public ExpressionNameTree qualifiedName(){
        return name;
    }

    @Override
    public IdentifierTree simpleName() {
        throw new UnsupportedOperationException();
    }
    
}