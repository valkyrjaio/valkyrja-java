/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.directory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.directory.Directory;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class DirectoryTest {

    private String originalBasePath;

    @BeforeEach
    void captureBasePath() {
        originalBasePath = Directory.basePath(null);
    }

    @AfterEach
    void restoreBasePath() {
        Directory.setBasePath(originalBasePath);
    }

    @Test
    void basePathReflectsSetBasePath() {
        Directory.setBasePath("/base");

        assertEquals("/base", Directory.basePath(null));
        assertEquals("/base/sub", Directory.basePath("sub"));
        assertEquals("/base/sub", Directory.basePath("/sub"));
    }

    @Test
    void namedPathsAreRelativeToBasePath() {
        Directory.setBasePath("/base");

        assertEquals("/base/app", Directory.appPath(null));
        assertEquals("/base/app/file", Directory.appPath("file"));
        assertEquals("/base/data", Directory.dataPath(null));
        assertEquals("/base/public", Directory.publicPath(null));
        assertEquals("/base/resources", Directory.resourcesPath(null));
        assertEquals("/base/src", Directory.srcPath(null));
        assertEquals("/base/storage", Directory.storagePath(null));
        assertEquals("/base/storage/framework", Directory.frameworkStoragePath(null));
        assertEquals("/base/storage/logs", Directory.logsStoragePath(null));
        assertEquals("/base/storage/framework/cache", Directory.frameworkStorageCachePath(null));
    }

    @Test
    void pathNormalizesLeadingSlash() {
        assertEquals("", Directory.path(null));
        assertEquals("", Directory.path(""));
        assertEquals("/foo", Directory.path("foo"));
        assertEquals("/foo", Directory.path("/foo"));
    }

    @Test
    void protectedConstructorIsInvocable() throws Exception {
        Constructor<Directory> constructor = Directory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
