/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
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
import sun.lwawt.LWWindowPeer;

public class CPlatformLWView extends CPlatformView {

    public CPlatformLWView() {
        super();
    }

    @Override
    public void initialize(LWWindowPeer peer, CPlatformResponder responder) {
        initializeBase(peer, responder);
    }

    @Override
    public long getAWTView() {
        return 0;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
    }

    @Override
    public SurfaceData replaceSurfaceData() {
        return null;
    }

    @Override
    public SurfaceData getSurfaceData() {
        return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public long getWindowLayerPtr() {
        return 0;
    }
}
