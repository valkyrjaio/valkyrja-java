/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.log.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.container.manager.Container;
import io.valkyrja.log.logger.FileLogger;
import io.valkyrja.log.logger.NullLogger;
import io.valkyrja.log.logger.contract.LoggerContract;
import io.valkyrja.log.provider.LogServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link LogServiceProvider}. */
final class LogServiceProviderTest {

    @Test
    void publishersExposeEveryLoggerBinding() {
        assertEquals(3, new LogServiceProvider().publishers().size());
    }

    @Test
    void publishFileLoggerBindsTheFileLogger() {
        var container = new Container();

        LogServiceProvider.publishFileLogger(container);

        assertInstanceOf(FileLogger.class, container.getSingleton(FileLogger.class));
    }

    @Test
    void publishNullLoggerBindsTheNullLogger() {
        var container = new Container();

        LogServiceProvider.publishNullLogger(container);

        assertInstanceOf(NullLogger.class, container.getSingleton(NullLogger.class));
    }

    @Test
    void publishLoggerDefaultsToTheFileLogger() {
        var container = new Container();
        var fileLogger = new FileLogger();
        container.setSingleton(FileLogger.class, fileLogger);

        LogServiceProvider.publishLogger(container);

        assertSame(fileLogger, container.getSingleton(LoggerContract.class));
    }

    /**
     * Registering the provider defers every publisher, so requesting a service runs its publisher
     * on demand — and any publisher it depends on first.
     *
     * <p>The publishers are deliberately <em>not</em> invoked by iterating {@link
     * LogServiceProvider#publishers()} directly: that map is a {@link java.util.Map#of Map.of},
     * whose iteration order the JVM randomizes per run, and {@code publishLogger} reads the {@link
     * FileLogger} singleton that {@code publishFileLogger} binds. Driving them in map order
     * therefore fails whenever {@code LoggerContract} happens to come first. Going through the
     * container is both order-independent and the path production actually takes.
     */
    @Test
    void everyPublisherBindsItsServiceThroughTheContainer() {
        var container = new Container();

        container.register(new LogServiceProvider());

        assertInstanceOf(LoggerContract.class, container.getSingleton(LoggerContract.class));
        assertInstanceOf(FileLogger.class, container.getSingleton(FileLogger.class));
        assertInstanceOf(NullLogger.class, container.getSingleton(NullLogger.class));
    }

    /**
     * The deferred logger resolves to the same {@link FileLogger} instance the file-logger
     * publisher bound, whichever order the container reaches them in.
     */
    @Test
    void theDeferredLoggerIsTheSameInstanceAsTheFileLogger() {
        var container = new Container();

        container.register(new LogServiceProvider());

        assertSame(
                container.getSingleton(FileLogger.class),
                container.getSingleton(LoggerContract.class));
    }
}
