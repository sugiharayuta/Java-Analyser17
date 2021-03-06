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

package jdk.jpackage.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class MacCertificate {
    private final String certificate;

    public MacCertificate(String certificate) {
        this.certificate = certificate;
    }

    public boolean isValid() {
        return verifyCertificate(this.certificate);
    }

    private static Path findCertificate(String certificate) {
        Path result = null;

        List<String> args = new ArrayList<>();
        args.add("/usr/bin/security");
        args.add("find-certificate");
        args.add("-c");
        args.add(certificate);
        args.add("-a");
        args.add("-p");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos)) {
            ProcessBuilder security = new ProcessBuilder(args);
            IOUtils.exec(security, false, ps);

            Path output = Files.createTempFile("tempfile", ".tmp");

            Files.copy(new ByteArrayInputStream(baos.toByteArray()),
                    output, StandardCopyOption.REPLACE_EXISTING);

            result = output;
        }
        catch (IOException ignored) {}

        return result;
    }

    private static Date findCertificateDate(String filename) {
        Date result = null;

        List<String> args = new ArrayList<>();
        args.add("/usr/bin/openssl");
        args.add("x509");
        args.add("-noout");
        args.add("-enddate");
        args.add("-in");
        args.add(filename);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos)) {
            ProcessBuilder security = new ProcessBuilder(args);
            IOUtils.exec(security, false, ps);
            String output = baos.toString();
            output = output.substring(output.indexOf("=") + 1);
            DateFormat df = new SimpleDateFormat(
                    "MMM dd kk:mm:ss yyyy z", Locale.ENGLISH);
            result = df.parse(output);
        } catch (IOException | ParseException ex) {
            Log.verbose(ex);
        }

        return result;
    }

    private static boolean verifyCertificate(String certificate) {
        boolean result = false;

        try {
            Path file = null;
            Date certificateDate = null;

            try {
                file = findCertificate(certificate);

                if (file != null) {
                    certificateDate = findCertificateDate(
                            file.toFile().getCanonicalPath());
                }
            }
            finally {
                if (file != null) {
                    Files.delete(file);
                }
            }

            if (certificateDate != null) {
                Calendar c = Calendar.getInstance();
                Date today = c.getTime();

                if (certificateDate.after(today)) {
                    result = true;
                }
            }
        }
        catch (IOException ignored) {}

        return result;
    }
}
