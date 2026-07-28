/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.log.provider;

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

    @Test
    void everyPublisherBindsItsServiceThroughTheContainer() {
        var container = new Container();
        var provider = new LogServiceProvider();

        provider.publishers().forEach((id, publisher) -> publisher.accept(container));

        assertInstanceOf(LoggerContract.class, container.getSingleton(LoggerContract.class));
        assertInstanceOf(FileLogger.class, container.getSingleton(FileLogger.class));
        assertInstanceOf(NullLogger.class, container.getSingleton(NullLogger.class));
    }
}
