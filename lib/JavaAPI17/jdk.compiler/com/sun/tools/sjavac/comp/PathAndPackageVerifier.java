/*
 * Copyright (c) 2014, 2021, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.tools.sjavac.comp;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.util.DefinedBy;
import com.sun.tools.javac.util.DefinedBy.Api;
import com.sun.tools.javac.util.Name;
import com.sun.tools.sjavac.Log;

public class PathAndPackageVerifier implements TaskListener {

    // Stores the set of compilation units whose source file path does not
    // match the package declaration.
    Set<CompilationUnitTree> misplacedCompilationUnits = new HashSet<>();

    @Override
    @DefinedBy(Api.COMPILER_TREE)
    public void finished(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.ANALYZE) {

            CompilationUnitTree cu = e.getCompilationUnit();
            if (cu == null)
                return;

            JavaFileObject jfo = cu.getSourceFile();
            if (jfo == null)
                return; // No source file -> package doesn't matter

            JCTree pkg = (JCTree) cu.getPackageName();
            if (pkg == null)
                return; // Default package. See JDK-8048144.

            Path dir = Paths.get(jfo.toUri()).normalize().getParent();
            if (!checkPathAndPackage(dir, pkg))
                misplacedCompilationUnits.add(cu);
        }

        if (e.getKind() == TaskEvent.Kind.COMPILATION) {
            for (CompilationUnitTree cu : misplacedCompilationUnits) {
                Log.error("Misplaced compilation unit.");
                Log.error("    Directory: " + Paths.get(cu.getSourceFile().toUri()).getParent());
                Log.error("    Package:   " + cu.getPackageName());
            }
        }
    }

    public boolean errorsDiscovered() {
        return misplacedCompilationUnits.size() > 0;
    }

    /* Returns true if dir matches pkgName.
     *
     * Examples:
     *     (a/b/c, a.b.c) gives true
     *     (i/j/k, i.x.k) gives false
     *
     * Currently (x/a/b/c, a.b.c) also gives true. See JDK-8059598.
     */
    private boolean checkPathAndPackage(Path dir, JCTree pkgName) {
        Iterator<String> pathIter = new ParentIterator(dir);
        Iterator<String> pkgIter = new EnclosingPkgIterator(pkgName);
        while (pathIter.hasNext() && pkgIter.hasNext()) {
            if (!pathIter.next().equals(pkgIter.next()))
                return false;
        }
        return !pkgIter.hasNext(); /*&& !pathIter.hasNext() See JDK-8059598 */
    }

    /* Iterates over the names of the parents of the given path:
     * Example: dir1/dir2/dir3  results in  dir3 -> dir2 -> dir1
     */
    private static class ParentIterator implements Iterator<String> {
        Path next;
        ParentIterator(Path initial) {
            next = initial;
        }
        @Override
        public boolean hasNext() {
            return next != null;
        }
        @Override
        public String next() {
            String tmp = next.getFileName().toString();
            next = next.getParent();
            return tmp;
        }
    }

    /* Iterates over the names of the enclosing packages:
     * Example: pkg1.pkg2.pkg3  results in  pkg3 -> pkg2 -> pkg1
     */
    private static class EnclosingPkgIterator implements Iterator<String> {
        JCTree next;
        EnclosingPkgIterator(JCTree initial) {
            next = initial;
        }
        @Override
        public boolean hasNext() {
            return next != null;
        }
        @Override
        public String next() {
            Name name;
            if (next instanceof JCIdent identNext) {
                name = identNext.name;
                next = null;
            } else {
                JCFieldAccess fa = (JCFieldAccess) next;
                name = fa.name;
                next = fa.selected;
            }
            return name.toString();
        }
    }
}
