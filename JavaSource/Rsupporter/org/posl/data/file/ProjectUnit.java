package org.posl.data.file;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.posl.compiler.JavaCompiler;
import org.posl.data.resolution.Accessible;
import org.posl.data.tree.CompilationUnitTree;
import org.posl.data.tree.ExpressionNameTree;
import org.posl.data.tree.IdentifierTree;

public final class ProjectUnit{
    
    volatile int DEBUG_SUCCEED = 0;
    volatile int DEBUG_FAILED = 0;

    private static final File API = new File("../../lib/JavaAPI17");

    private final JavaModule unnamedModule = new JavaModule(CompilationUnitTree.UNNAMED_MODULE);
    private final JavaPackage unnamedPackage = new JavaPackage(ExpressionNameTree.EMPTY, new ConcurrentHashMap<>());

    private final Map<ExpressionNameTree, JavaModule> moduleTable = new ConcurrentHashMap<>();
    private final Map<ExpressionNameTree, JavaPackage> packageTable = new ConcurrentHashMap<>();
    private final JavaCompiler compiler = new JavaCompiler();

    {
        moduleTable.put(ExpressionNameTree.EMPTY, unnamedModule);
        packageTable.put(ExpressionNameTree.EMPTY, unnamedPackage);
    }

    public ProjectUnit(File sourcePath){
        System.out.println("core : " +Runtime.getRuntime().availableProcessors());
        long time = System.currentTimeMillis();
        Arrays.asList(API.listFiles(dir -> dir.isDirectory())).parallelStream().forEach(dir -> {resolvePackage(dir, ExpressionNameTree.EMPTY, unnamedModule);});
        time = System.currentTimeMillis() - time;
        // if(sourcePath.isDirectory()){
        //     resolvePackage(sourcePath, ExpressionNameTree.EMPTY, unnamedModule);
        // }
        System.out.println(String.format("Failed parsing in %d files. (in %d)", DEBUG_FAILED, DEBUG_SUCCEED + DEBUG_FAILED));
        System.out.println(String.format("%d mili seconds", time));
    }

    private JavaPackage resolvePackage(File dir, ExpressionNameTree name, JavaModule associated){
        for(File f : dir.listFiles((d, s) -> s.equals("module-info.java"))){
            associated = new JavaModule(compiler.compile(f));
            moduleTable.put(associated.qualifiedName(), associated);
        }

        Map<IdentifierTree, Accessible> contents = new ConcurrentHashMap<>();

        for(File f : dir.listFiles((d, s) -> !s.equals("module-info.java"))){
            if(f.isDirectory()){
                var directoryName = new IdentifierTree(f.getName());
                JavaPackage subPackage = resolvePackage(f, new ExpressionNameTree(name, directoryName), associated);
                contents.put(directoryName, subPackage);
                associated.associatePackage(subPackage);
            }else{
                String fileName = f.getName();
                int separator = fileName.lastIndexOf(".");
                if(separator > 0 && fileName.substring(separator).equals(".java")){
                    var sourceName = new IdentifierTree(fileName.substring(0, separator));
                    contents.put(sourceName, new JavaSourceFile(new ExpressionNameTree(name, sourceName), f));
                    if(compiler.compile(f) == CompilationUnitTree.ERROR){
                        System.out.println(String.format("Failed parsing \"%s\"(%s)", new ExpressionNameTree(name, sourceName).toString(), associated.qualifiedName().toString()));
                        DEBUG_FAILED++;
                    }else{
                        DEBUG_SUCCEED++;
                    }
                }
            }
        }

        JavaPackage p = packageTable.get(name);
        if(p == null){
            p = new JavaPackage(name, contents);
        }else{
            p.accept(contents);
        }
        return p;
    }

}
