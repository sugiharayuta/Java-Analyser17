/*
 * Copyright (c) 2012, 2021, Oracle and/or its affiliates. All rights reserved.
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
package jdk.jfr.internal.dcmd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jdk.jfr.FlightRecorder;
import jdk.jfr.Recording;
import jdk.jfr.internal.JVM;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.SecuritySupport.SafePath;
import jdk.jfr.internal.Utils;

/**
 * Base class for JFR diagnostic commands
 *
 */
abstract class AbstractDCmd {

    private final StringBuilder currentLine = new StringBuilder(80);
    private final List<String> lines = new ArrayList<>();
    private String source;

    // Called by native
    public abstract String[] printHelp();

    // Called by native. The number of arguments for each command is
    // reported to the DCmdFramework as a hardcoded number in native.
    // This is to avoid an upcall as part of DcmdFramework enumerating existing commands.
    // Remember to keep the two sides in synch.
    public abstract Argument[] getArgumentInfos();

    // Called by native
    protected abstract void execute(ArgumentParser parser) throws DCmdException;


    // Called by native
    public final String[] execute(String source, String arg, char delimiter) throws DCmdException {
        this.source = source;
        try {
            boolean log = Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG);
            if (log) {
                System.out.println(arg);
                Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing " + this.getClass().getSimpleName() + ": " + arg);
            }
            ArgumentParser parser = new ArgumentParser(getArgumentInfos(), arg, delimiter);
            parser.parse();
            if (log) {
                Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "DCMD options: " + parser.getOptions());
                if (parser.hasExtendedOptions()) {
                    Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "JFC options: " + parser.getExtendedOptions());
                }
            }
            execute(parser);
            return getResult();
       }
       catch (IllegalArgumentException iae) {
            DCmdException e = new DCmdException(iae.getMessage());
            e.addSuppressed(iae);
            throw e;
        }
    }


    protected final FlightRecorder getFlightRecorder() {
        return FlightRecorder.getFlightRecorder();
    }

    protected final String[] getResult() {
        return lines.toArray(new String[lines.size()]);
    }

    protected void logWarning(String message) {
        if (source.equals("internal")) { // -XX:StartFlightRecording
            Logger.log(LogTag.JFR_START, LogLevel.WARN, message);
        } else { // DiagnosticMXBean or JCMD
            println("Warning! " + message);
        }
    }

    public String getPid() {
        // Invoking ProcessHandle.current().pid() would require loading more
        // classes during startup so instead JVM.getJVM().getPid() is used.
        // The pid will not be exposed to running Java application, only when starting
        // JFR from command line (-XX:StartFlightRecording) or jcmd (JFR.start and JFR.check)
        return JVM.getJVM().getPid();
    }

    protected final SafePath resolvePath(Recording recording, String filename) throws InvalidPathException {
        if (filename == null) {
            return makeGenerated(recording, Paths.get("."));
        }
        Path path = Paths.get(filename);
        if (Files.isDirectory(path)) {
            return makeGenerated(recording, path);
        }
        return new SafePath(path.toAbsolutePath().normalize());
    }

    private SafePath makeGenerated(Recording recording, Path directory) {
        return new SafePath(directory.toAbsolutePath().resolve(Utils.makeFilename(recording)).normalize());
    }

    protected final Recording findRecording(String name) throws DCmdException {
        try {
            return findRecordingById(Integer.parseInt(name));
        } catch (NumberFormatException nfe) {
            // User specified a name, not an id.
            return findRecordingByName(name);
        }
    }

    protected final void reportOperationComplete(String actionPrefix, String name, SafePath file) {
        print(actionPrefix);
        print(" recording");
        if (name != null) {
            print(" \"" + name + "\"");
        }
        if (file != null) {
            print(",");
            try {
                print(" ");
                long bytes = SecuritySupport.getFileSize(file);
                printBytes(bytes);
            } catch (IOException e) {
                // Ignore, not essential
            }
            println(" written to:");
            println();
            printPath(file);
        } else {
            println(".");
        }
    }

    protected final List<Recording> getRecordings() {
        List<Recording> list = new ArrayList<>(getFlightRecorder().getRecordings());
        Collections.sort(list, Comparator.comparing(Recording::getId));
        return list;
    }

    static String quoteIfNeeded(String text) {
        if (text.contains(" ")) {
            return "\\\"" + text + "\\\"";
        } else {
            return text;
        }
    }

    protected final void println() {
        lines.add(currentLine.toString());
        currentLine.setLength(0);
    }

    protected final void print(String s) {
        currentLine.append(s);
    }

    protected final void print(String s, Object... args) {
        currentLine.append(String.format(s, args));
    }

    protected final void println(String s, Object... args) {
        print(s, args);
        println();
    }

    protected final void printBytes(long bytes) {
        print(Utils.formatBytes(bytes));
    }

    protected final void printTimespan(Duration timespan, String separator) {
        print(Utils.formatTimespan(timespan, separator));
    }

    protected final void printPath(SafePath path) {
        if (path == null) {
            print("N/A");
            return;
        }
        try {
            printPath(SecuritySupport.getAbsolutePath(path).toPath());
        } catch (IOException ioe) {
            printPath(path.toPath());
        }
    }

    protected final void printPath(Path path) {
        try {
            println(path.toAbsolutePath().toString());
        } catch (SecurityException e) {
            // fall back on filename
            println(path.toString());
        }
    }

    private Recording findRecordingById(int id) throws DCmdException {
        for (Recording r : getFlightRecorder().getRecordings()) {
            if (r.getId() == id) {
                return r;
            }
        }
        throw new DCmdException("Could not find %d.\n\nUse JFR.check without options to see list of all available recordings.", id);
    }

    private Recording findRecordingByName(String name) throws DCmdException {
        for (Recording recording : getFlightRecorder().getRecordings()) {
            if (name.equals(recording.getName())) {
                return recording;
            }
        }
        throw new DCmdException("Could not find %s.\n\nUse JFR.check without options to see list of all available recordings.", name);
    }

    protected final String exampleRepository() {
        if ("\r\n".equals(System.lineSeparator())) {
            return "C:\\Repositories";
        } else {
            return "/Repositories";
        }
    }

    protected final String exampleFilename() {
        if ("\r\n".equals(System.lineSeparator())) {
            return "C:\\Users\\user\\recording.jfr";
        } else {
            return "/recordings/recording.jfr";
        }
    }

    protected final String exampleDirectory() {
        if ("\r\n".equals(System.lineSeparator())) {
            return "C:\\Directory\\recordings";
        } else {
            return "/directory/recordings";
        }
    }
}
