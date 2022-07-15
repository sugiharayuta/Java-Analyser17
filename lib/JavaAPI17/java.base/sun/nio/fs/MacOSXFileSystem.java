/*
 * Copyright (c) 2008, 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.fs;

import java.util.regex.Pattern;

import static sun.nio.fs.MacOSXNativeDispatcher.*;

/**
 * MacOS implementation of FileSystem
 */

class MacOSXFileSystem extends BsdFileSystem {

    MacOSXFileSystem(UnixFileSystemProvider provider, String dir) {
        super(provider, dir);
    }

    // match in unicode canon_eq
    Pattern compilePathMatchPattern(String expr) {
        return Pattern.compile(expr, Pattern.CANON_EQ) ;
    }

    @Override
    String normalizeNativePath(String path) {
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c > 0x80)
                return new String(normalizepath(path.toCharArray(),
                                  kCFStringNormalizationFormD));
        }
        return path;
    }

    @Override
    String normalizeJavaPath(String path) {
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) > 0x80)
                return new String(normalizepath(path.toCharArray(),
                                  kCFStringNormalizationFormC));
        }
        return path;
    }

}
