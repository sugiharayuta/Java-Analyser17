/*
 * Copyright (c) 2016, 2020, Oracle and/or its affiliates. All rights reserved.
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

package jdk.jfr.internal.handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jdk.jfr.EventType;
import jdk.jfr.internal.EventControl;
import jdk.jfr.internal.JVM;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.internal.PrivateAccess;
import jdk.jfr.internal.StringPool;

// Users should not be subclass for security reasons.
public abstract class EventHandler {
    // Accessed by generated sub class
    protected final PlatformEventType platformEventType;

    private final EventType eventType;
    private final EventControl eventControl;

    // Accessed by generated sub class
    EventHandler(boolean registered, EventType eventType, EventControl eventControl) {
        this.eventType = eventType;
        this.platformEventType = PrivateAccess.getInstance().getPlatformEventType(eventType);
        this.eventControl = eventControl;
        platformEventType.setRegistered(registered);
    }

    protected final StringPool createStringFieldWriter() {
        return new StringPool();
    }

    // Accessed by generated code in event class
    public final boolean shouldCommit(long duration) {
        return isEnabled() && duration >= platformEventType.getThresholdTicks();
    }

    // Accessed by generated code in event class
    // Accessed by generated sub class
    public final boolean isEnabled() {
        return platformEventType.isCommittable();
    }

    public final EventType getEventType() {
        return eventType;
    }

    public final PlatformEventType getPlatformEventType() {
        return platformEventType;
    }

    public final EventControl getEventControl() {
        return eventControl;
    }

    public static long timestamp() {
        return JVM.counterTime();
    }

    public static long duration(long startTime) {
        if (startTime == 0) {
            // User forgot to invoke begin, or instrumentation was
            // added after the user invoked begin.
            // Returning 0 will make it an instant event
            return 0;
        }
        return timestamp() - startTime;
    }

    // Prevent a malicious user from instantiating a generated event handlers.
    @Override
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private final void writeObject(ObjectOutputStream out) throws IOException {
        throw new IOException("Object cannot be serialized");
    }

    private final void readObject(ObjectInputStream in) throws IOException {
        throw new IOException("Class cannot be deserialized");
    }

    public boolean isRegistered() {
        return platformEventType.isRegistered();
    }

    public boolean setRegistered(boolean registered) {
       return platformEventType.setRegistered(registered);
    }

    public void write(long start, long duration, String host, String address, int port, long timeout, long bytesRead, boolean endOfSTream) {
        throwError("SocketReadEvent");
    }

    public void write(long start, long duration, String host, String address, int port, long bytesWritten) {
        throwError("SocketWriteEvent");
    }

    public void write(long start, long duration, String path, boolean metadata) {
        throwError("FileForceEvent");
    }

    public void write(long start, long duration, String path, long bytesRead, boolean endOfFile) {
        throwError("FileReadEvent");
    }

    public void write(long start, long duration, String path, long bytesWritten) {
        throwError("FileWriteEvent");
    }

    public void write(long start, long duration, String path, Class<?> exceptionClass)  {
        throwError("ExceptionThrownEvent or ErrorThrownEvent");
    }

    private void throwError(String classes) {
        throw new InternalError("Method parameters don't match fields in class " + classes);
    }
}
