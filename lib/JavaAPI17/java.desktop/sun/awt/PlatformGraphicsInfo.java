/*
 * Copyright (c) 2019, 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;

@SuppressWarnings("removal")
public class PlatformGraphicsInfo {

    static {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            System.loadLibrary("awt");
            return null;
        });
    }

    public static GraphicsEnvironment createGE() {
        return new CGraphicsEnvironment();
    }

    public static Toolkit createToolkit() {
        return new sun.lwawt.macosx.LWCToolkit();
    }

    /**
     * Returns true if the WindowServer is available, false otherwise.
     *
     * @return true if the WindowServer is available, false otherwise
     */
    public static native boolean isInAquaSession();

    public static boolean getDefaultHeadlessProperty() {
         return !isInAquaSession();
    }

    /*
     * Called from java.awt.GraphicsEnvironment when
     * getDefaultHeadlessProperty() has returned true, and
     * the application has called an API that requires headful.
     */
    public static String getDefaultHeadlessMessage() {
        return
            "\nThe application is not running in a desktop session,\n" +
            "but this program performed an operation which requires it.";
    }

}
