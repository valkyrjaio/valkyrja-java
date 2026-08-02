/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.logger;

import io.valkyrja.application.directory.Directory;
import io.valkyrja.log.enum_.LogLevel;
import io.valkyrja.log.logger.abstract_.Logger;
import io.valkyrja.log.throwable.exception.LogFileWriteException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Logger that appends to a dated file in the logs storage directory.
 *
 * <p>The zero-dependency default: it uses only the JDK, mirroring the PHP port's file-writing
 * default (a Monolog stream handler over {@code Directory::logsStoragePath()}) without pulling a
 * logging backend onto the classpath. A consumer that wants a different backend binds its own
 * {@link io.valkyrja.log.logger.contract.LoggerContract} implementation instead.
 *
 * <p>Each entry is one line — {@code [timestamp] level: message {context}} — with a throwable's
 * stack trace appended on the following lines. The file is named {@code valkyrja-YYYY-MM-DD.log},
 * so a new file starts each day.
 */
public class FileLogger extends Logger {

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final Path file;

    /** Build a logger writing to today's file in the logs storage directory. */
    public FileLogger() {
        this(defaultFile());
    }

    /**
     * Build a logger writing to a given file.
     *
     * @param file the file to append to
     */
    public FileLogger(Path file) {
        this.file = file;
    }

    /**
     * Resolve today's log file in the logs storage directory.
     *
     * @return the log file path
     */
    public static Path defaultFile() {
        return Path.of(Directory.logsStoragePath(null))
                .resolve("valkyrja-" + LocalDate.now() + ".log");
    }

    @Override
    public void debug(String message, Map<String, Object> context) {
        write(LogLevel.DEBUG, message, context);
    }

    @Override
    public void info(String message, Map<String, Object> context) {
        write(LogLevel.INFO, message, context);
    }

    @Override
    public void notice(String message, Map<String, Object> context) {
        write(LogLevel.NOTICE, message, context);
    }

    @Override
    public void warning(String message, Map<String, Object> context) {
        write(LogLevel.WARNING, message, context);
    }

    @Override
    public void error(String message, Map<String, Object> context) {
        write(LogLevel.ERROR, message, context);
    }

    @Override
    public void critical(String message, Map<String, Object> context) {
        write(LogLevel.CRITICAL, message, context);
    }

    @Override
    public void alert(String message, Map<String, Object> context) {
        write(LogLevel.ALERT, message, context);
    }

    @Override
    public void emergency(String message, Map<String, Object> context) {
        write(LogLevel.EMERGENCY, message, context);
    }

    @Override
    public void throwable(Throwable throwable, String message, Map<String, Object> context) {
        StringWriter trace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(trace));

        write(LogLevel.ERROR, message + System.lineSeparator() + trace, context);
    }

    /**
     * Append a single entry to the log file.
     *
     * @param level the log level
     * @param message the message
     * @param context the context appended to the message, if any
     */
    protected void write(LogLevel level, String message, Map<String, Object> context) {
        String entry = format(level, message, context);

        try {
            Path directory = file.getParent();

            if (directory != null) {
                Files.createDirectories(directory);
            }

            Files.writeString(
                    file,
                    entry,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new LogFileWriteException("Unable to write to the log file " + file, e);
        }
    }

    /**
     * Format a single log entry.
     *
     * @param level the log level
     * @param message the message
     * @param context the context appended to the message, if any
     * @return the formatted entry, terminated by a line separator
     */
    protected String format(LogLevel level, String message, Map<String, Object> context) {
        String entry =
                "["
                        + LocalDateTime.now().format(TIMESTAMP)
                        + "] "
                        + level.getValue()
                        + ": "
                        + message;

        if (context != null && !context.isEmpty()) {
            entry += " " + context;
        }

        return entry + System.lineSeparator();
    }
}
