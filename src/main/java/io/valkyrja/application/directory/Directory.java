/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.directory;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class Directory {

    protected Directory() {
        /* This class should be extended, not instantiated directly */
    }

    private static String basePath = System.getProperty("user.dir");

    protected static final String APP_PATH = "app";
    protected static final String DATA_PATH = "data";
    protected static final String PUBLIC_PATH = "public";
    protected static final String RESOURCES_PATH = "resources";
    protected static final String SRC_PATH = "src";
    protected static final String STORAGE_PATH = "storage";
    protected static final String FRAMEWORK_STORAGE_PATH = "framework";
    protected static final String CACHE_STORAGE_PATH = "cache";
    protected static final String LOGS_STORAGE_PATH = "logs";

    public static void setBasePath(String path) {
        basePath = path;
    }

    public static String appPath(@Nullable String path) {
        return basePath(APP_PATH + path(path));
    }

    public static String basePath(@Nullable String path) {
        return basePath + path(path);
    }

    public static String dataPath(@Nullable String path) {
        return basePath(DATA_PATH + path(path));
    }

    public static String publicPath(@Nullable String path) {
        return basePath(PUBLIC_PATH + path(path));
    }

    public static String resourcesPath(@Nullable String path) {
        return basePath(RESOURCES_PATH + path(path));
    }

    public static String srcPath(@Nullable String path) {
        return basePath(SRC_PATH + path(path));
    }

    public static String storagePath(@Nullable String path) {
        return basePath(STORAGE_PATH + path(path));
    }

    public static String frameworkStoragePath(@Nullable String path) {
        return storagePath(FRAMEWORK_STORAGE_PATH + path(path));
    }

    public static String logsStoragePath(@Nullable String path) {
        return storagePath(LOGS_STORAGE_PATH + path(path));
    }

    public static String frameworkStorageCachePath(@Nullable String path) {
        return frameworkStoragePath(CACHE_STORAGE_PATH + path(path));
    }

    public static String path(@Nullable String path) {
        path = Objects.requireNonNullElse(path, "");

        return !path.isEmpty() && !path.startsWith("/") ? "/" + path : path;
    }
}
