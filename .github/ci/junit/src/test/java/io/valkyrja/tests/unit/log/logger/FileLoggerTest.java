/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.log.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.log.enum_.LogLevel;
import io.valkyrja.log.logger.FileLogger;
import io.valkyrja.log.throwable.exception.LogFileWriteException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Test the {@link FileLogger}. */
final class FileLoggerTest {

    @TempDir Path directory;

    @Test
    void writesEachLevelWithItsOwnName() throws IOException {
        Path file = directory.resolve("valkyrja.log");
        FileLogger logger = new FileLogger(file);

        Map<LogLevel, BiConsumer<String, Map<String, Object>>> levels =
                Map.of(
                        LogLevel.DEBUG, logger::debug,
                        LogLevel.INFO, logger::info,
                        LogLevel.NOTICE, logger::notice,
                        LogLevel.WARNING, logger::warning,
                        LogLevel.ERROR, logger::error,
                        LogLevel.CRITICAL, logger::critical,
                        LogLevel.ALERT, logger::alert,
                        LogLevel.EMERGENCY, logger::emergency);

        levels.forEach((level, method) -> method.accept("message for " + level, Map.of()));

        List<String> lines = Files.readAllLines(file);

        assertEquals(levels.size(), lines.size());

        for (LogLevel level : levels.keySet()) {
            assertTrue(
                    lines.stream()
                            .anyMatch(
                                    line ->
                                            line.contains(level.getValue() + ": ")
                                                    && line.endsWith("message for " + level)),
                    "Expected an entry for " + level);
        }
    }

    /** Every level also reaches the file through the abstract logger's level dispatch. */
    @ParameterizedTest
    @EnumSource(LogLevel.class)
    void dispatchesEveryLevelThroughLog(LogLevel level) throws IOException {
        Path file = directory.resolve(level + ".log");

        new FileLogger(file).log(level, "dispatched", Map.of());

        assertTrue(Files.readString(file).contains(level.getValue() + ": dispatched"));
    }

    @Test
    void appendsContextWhenPresent() throws IOException {
        Path file = directory.resolve("context.log");

        new FileLogger(file).info("with context", Map.of("key", "value"));

        assertTrue(Files.readString(file).contains("with context {key=value}"));
    }

    @Test
    void omitsContextWhenEmptyOrNull() throws IOException {
        Path file = directory.resolve("no-context.log");
        FileLogger logger = new FileLogger(file);

        logger.info("empty context", Map.of());
        logger.info("null context", null);

        List<String> lines = Files.readAllLines(file);

        assertTrue(lines.get(0).endsWith("empty context"));
        assertTrue(lines.get(1).endsWith("null context"));
    }

    @Test
    void writesTheStackTraceForAThrowable() throws IOException {
        Path file = directory.resolve("throwable.log");

        new FileLogger(file)
                .throwable(new IllegalStateException("boom"), "it failed", Map.of("id", 7));

        String contents = Files.readString(file);

        assertTrue(contents.contains("error: it failed"));
        assertTrue(contents.contains("java.lang.IllegalStateException: boom"));
        assertTrue(contents.contains("at io.valkyrja.tests.unit.log.logger.FileLoggerTest"));
        assertTrue(contents.contains("{id=7}"));
    }

    @Test
    void appendsToAnExistingFileRatherThanTruncatingIt() throws IOException {
        Path file = directory.resolve("append.log");
        FileLogger logger = new FileLogger(file);

        logger.info("first", Map.of());
        logger.info("second", Map.of());

        assertEquals(2, Files.readAllLines(file).size());
    }

    @Test
    void createsMissingParentDirectories() throws IOException {
        Path file = directory.resolve("nested/deeper/valkyrja.log");

        new FileLogger(file).info("created", Map.of());

        assertTrue(Files.exists(file));
    }

    @Test
    void writesWhenTheFileHasNoParentDirectory() throws IOException {
        // A bare relative file name has no parent, so the directory creation is skipped.
        Path file = Path.of("file-logger-no-parent.log");

        try {
            new FileLogger(file).info("no parent", Map.of());

            assertTrue(Files.readString(file).contains("no parent"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void throwsWhenTheFileCannotBeWritten() throws IOException {
        // A path whose parent is an existing regular file cannot be created as a directory.
        Path blocker = directory.resolve("blocker");
        Files.writeString(blocker, "not a directory");

        FileLogger logger = new FileLogger(blocker.resolve("valkyrja.log"));

        LogFileWriteException exception =
                assertThrows(LogFileWriteException.class, () -> logger.info("nope", Map.of()));

        assertTrue(exception.getMessage().startsWith("Unable to write to the log file "));
        assertNotNull(exception.getCause());
    }

    @Test
    void defaultsToADatedFileInTheLogsStorageDirectory() {
        Path file = FileLogger.defaultFile();

        assertTrue(file.getFileName().toString().startsWith("valkyrja-"));
        assertTrue(file.getFileName().toString().endsWith(".log"));
        assertFalse(file.getFileName().toString().contains("null"));
    }

    @Test
    void buildsTheDefaultFileWithoutArguments() {
        assertNotNull(new FileLogger());
    }
}
