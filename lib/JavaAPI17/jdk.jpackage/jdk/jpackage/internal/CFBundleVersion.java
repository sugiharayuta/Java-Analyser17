/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jpackage.internal;

import java.math.BigInteger;


final class CFBundleVersion {
    /**
     * Parse the given string as OSX CFBundleVersion.
     * CFBundleVersion (String - iOS, OS X) specifies the build version number of
     * the bundle, which identifies an iteration (released or unreleased) of the
     * bundle. The build version number should be a string comprised of at most three
     * non-negative, period-separated integers with the first integer being greater
     * than zero.
     * @throws IllegalArgumentException
     */
    static DottedVersion of(String value) {
        DottedVersion ver = new DottedVersion(value);

        BigInteger[] components = ver.getComponents();
        if (components.length > 3) {
            throw new IllegalArgumentException(I18N.getString(
                    "message.version-string-too-many-components"));
        }

        if (BigInteger.ZERO.equals(components[0])) {
            throw new IllegalArgumentException(I18N.getString(
                    "message.version-string-first-number-not-zero"));
        }

        return ver;
    }
}
