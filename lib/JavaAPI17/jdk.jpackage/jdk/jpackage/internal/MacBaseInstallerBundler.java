/*
 * Copyright (c) 2014, 2021, Oracle and/or its affiliates. All rights reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static jdk.jpackage.internal.StandardBundlerParam.APP_NAME;
import static jdk.jpackage.internal.StandardBundlerParam.INSTALLER_NAME;
import static jdk.jpackage.internal.StandardBundlerParam.INSTALL_DIR;
import static jdk.jpackage.internal.StandardBundlerParam.PREDEFINED_APP_IMAGE;
import static jdk.jpackage.internal.StandardBundlerParam.VERSION;

public abstract class MacBaseInstallerBundler extends AbstractBundler {

    public final BundlerParamInfo<Path> APP_IMAGE_TEMP_ROOT =
            new StandardBundlerParam<>(
            "mac.app.imageRoot",
            Path.class,
            params -> {
                Path imageDir = IMAGES_ROOT.fetchFrom(params);
                try {
                    if (!IOUtils.exists(imageDir)) {
                        Files.createDirectories(imageDir);
                    }
                    return Files.createTempDirectory(
                            imageDir, "image-");
                } catch (IOException e) {
                    return imageDir.resolve(getID()+ ".image");
                }
            },
            (s, p) -> Path.of(s));

    public static final BundlerParamInfo<String> SIGNING_KEY_USER =
            new StandardBundlerParam<>(
            Arguments.CLIOptions.MAC_SIGNING_KEY_NAME.getId(),
            String.class,
            params -> "",
            null);

    public static final BundlerParamInfo<String> SIGNING_KEYCHAIN =
            new StandardBundlerParam<>(
            Arguments.CLIOptions.MAC_SIGNING_KEYCHAIN.getId(),
            String.class,
            params -> "",
            null);

    public static final BundlerParamInfo<String> MAC_INSTALLER_NAME =
            new StandardBundlerParam<> (
            "mac.installerName",
            String.class,
            params -> {
                String nm = INSTALLER_NAME.fetchFrom(params);
                if (nm == null) return null;

                String version = VERSION.fetchFrom(params);
                if (version == null) {
                    return nm;
                } else {
                    return nm + "-" + version;
                }
            },
            (s, p) -> s);

    protected static String getInstallDir(
            Map<String, ? super Object>  params, boolean defaultOnly) {
        String returnValue = INSTALL_DIR.fetchFrom(params);
        if (defaultOnly && returnValue != null) {
            Log.info(I18N.getString("message.install-dir-ignored"));
            returnValue = null;
        }
        if (returnValue == null) {
            if (StandardBundlerParam.isRuntimeInstaller(params)) {
                returnValue = "/Library/Java/JavaVirtualMachines";
            } else {
               returnValue = "/Applications";
            }
        }
        return returnValue;
    }

    public MacBaseInstallerBundler() {
        appImageBundler = new MacAppBundler().setDependentTask(true);
    }

    protected void validateAppImageAndBundeler(
            Map<String, ? super Object> params) throws ConfigException {
        if (PREDEFINED_APP_IMAGE.fetchFrom(params) != null) {
            Path applicationImage = PREDEFINED_APP_IMAGE.fetchFrom(params);
            if (!IOUtils.exists(applicationImage)) {
                throw new ConfigException(
                        MessageFormat.format(I18N.getString(
                                "message.app-image-dir-does-not-exist"),
                                PREDEFINED_APP_IMAGE.getID(),
                                applicationImage.toString()),
                        MessageFormat.format(I18N.getString(
                                "message.app-image-dir-does-not-exist.advice"),
                                PREDEFINED_APP_IMAGE.getID()));
            }
            if (APP_NAME.fetchFrom(params) == null) {
                throw new ConfigException(
                        I18N.getString("message.app-image-requires-app-name"),
                        I18N.getString(
                            "message.app-image-requires-app-name.advice"));
            }
        } else {
            appImageBundler.validate(params);
        }
    }

    protected Path prepareAppBundle(Map<String, ? super Object> params)
            throws PackagerException {
        Path predefinedImage =
                StandardBundlerParam.getPredefinedAppImage(params);
        if (predefinedImage != null) {
            return predefinedImage;
        }
        Path appImageRoot = APP_IMAGE_TEMP_ROOT.fetchFrom(params);

        return appImageBundler.execute(params, appImageRoot);
    }

    @Override
    public String getBundleType() {
        return "INSTALLER";
    }

    public static String findKey(String keyPrefix, String teamName, String keychainName) {

        boolean useAsIs = teamName.startsWith(keyPrefix)
                || teamName.startsWith("Developer ID")
                || teamName.startsWith("3rd Party Mac");

        String key = (useAsIs) ? teamName : (keyPrefix + teamName);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos)) {
            List<String> searchOptions = new ArrayList<>();
            searchOptions.add("/usr/bin/security");
            searchOptions.add("find-certificate");
            searchOptions.add("-c");
            searchOptions.add(key);
            searchOptions.add("-a");
            if (keychainName != null && !keychainName.isEmpty()) {
                searchOptions.add(keychainName);
            }

            ProcessBuilder pb = new ProcessBuilder(searchOptions);

            IOUtils.exec(pb, false, ps);
            Pattern p = Pattern.compile("\"alis\"<blob>=\"([^\"]+)\"");
            Matcher m = p.matcher(baos.toString());
            if (!m.find()) {
                Log.error(MessageFormat.format(I18N.getString(
                        "error.cert.not.found"), key, keychainName));
                return null;
            }
            String matchedKey = m.group(1);
            if (m.find()) {
                Log.error(MessageFormat.format(I18N.getString(
                        "error.multiple.certs.found"), key, keychainName));
            }
            return matchedKey;
        } catch (IOException ioe) {
            Log.verbose(ioe);
            return null;
        }
    }

    private final Bundler appImageBundler;
}
