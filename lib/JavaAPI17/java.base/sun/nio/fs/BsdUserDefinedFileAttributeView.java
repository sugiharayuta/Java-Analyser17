/*
 * Copyright (c) 2008, 2015, Oracle and/or its affiliates. All rights reserved.
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

class BsdUserDefinedFileAttributeView
    extends UnixUserDefinedFileAttributeView
{

    BsdUserDefinedFileAttributeView(UnixPath file, boolean followLinks) {
        super(file, followLinks);
    }

    @Override
    protected int maxNameLength() {
        // see XATTR_MAXNAMELEN in https://github.com/apple/darwin-xnu/blob/master/bsd/sys/xattr.h
        return 127;
    }

}
