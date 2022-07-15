/*
 * Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Allows different platforms to call different native methods
 * for read and write operations.
 */

class SocketDispatcher extends NativeDispatcher {
    SocketDispatcher() { }

    /**
     * Reads up to len bytes from a socket with special handling for "connection
     * reset".
     *
     * @throws sun.net.ConnectionResetException if connection reset is detected
     * @throws IOException if another I/O error occurs
     */
    int read(FileDescriptor fd, long address, int len) throws IOException {
        return read0(fd, address, len);
    }

    /**
     * Scattering read from a socket into len buffers with special handling for
     * "connection reset".
     *
     * @throws sun.net.ConnectionResetException if connection reset is detected
     * @throws IOException if another I/O error occurs
     */
    long readv(FileDescriptor fd, long address, int len) throws IOException {
        return readv0(fd, address, len);
    }

    int write(FileDescriptor fd, long address, int len) throws IOException {
        return FileDispatcherImpl.write0(fd, address, len);
    }

    long writev(FileDescriptor fd, long address, int len) throws IOException {
        return FileDispatcherImpl.writev0(fd, address, len);
    }

    void close(FileDescriptor fd) throws IOException {
        FileDispatcherImpl.close0(fd);
    }

    void preClose(FileDescriptor fd) throws IOException {
        FileDispatcherImpl.preClose0(fd);
    }

    // -- Native methods --

    private static native int read0(FileDescriptor fd, long address, int len)
        throws IOException;

    private static native long readv0(FileDescriptor fd, long address, int len)
        throws IOException;

    static {
        IOUtil.load();
    }
}
