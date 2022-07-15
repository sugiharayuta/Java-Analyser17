/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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

package sun.lwawt.macosx;

import sun.java2d.SurfaceData;
import sun.java2d.NullSurfaceData;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import sun.lwawt.LWWindowPeer;

/**
 * Common layer class between OpenGl and Metal.
 */
public abstract class CFLayer extends CFRetainedResource {
    protected SurfaceData surfaceData; // represents intermediate buffer (texture)
    protected LWWindowPeer peer;

    protected CFLayer(long ptr, boolean disposeOnAppKitThread) {
        super(ptr, disposeOnAppKitThread);
    }

    public abstract SurfaceData replaceSurfaceData();

    @Override
    public void dispose() {
        super.dispose();
    }

    public long getPointer() {
        return ptr;
    }

    public SurfaceData getSurfaceData() {
        return surfaceData;
    }

    public Rectangle getBounds() {
        return peer.getBounds();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return peer.getGraphicsConfiguration();
    }

    public boolean isOpaque() {
        return !peer.isTranslucent();
    }

    public int getTransparency() {
        return isOpaque() ? Transparency.OPAQUE : Transparency.TRANSLUCENT;
    }

    public Object getDestination() {
        return peer.getTarget();
    }
}
