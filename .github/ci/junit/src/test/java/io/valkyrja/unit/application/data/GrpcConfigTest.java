/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.provider.GrpcApplicationComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link GrpcConfig} defaults. */
final class GrpcConfigTest {

    @Test
    void defaultsCarryTheGrpcApplicationComponentProviderAndPort() {
        GrpcConfig config = new GrpcConfig();

        assertEquals("App", config.namespace());
        assertEquals(50051, config.port());
        assertFalse(config.debugMode());
        assertEquals(1, config.providers().size());
        assertTrue(config.providers().get(0) instanceof GrpcApplicationComponentProvider);
    }

    @Test
    void defaultMiddlewareListsAreEmpty() {
        GrpcConfig config = new GrpcConfig();

        assertTrue(config.callReceivedMiddleware().isEmpty());
        assertTrue(config.routeMatchedMiddleware().isEmpty());
        assertTrue(config.routeNotMatchedMiddleware().isEmpty());
        assertTrue(config.routeDispatchedMiddleware().isEmpty());
        assertTrue(config.throwableCaughtMiddleware().isEmpty());
        assertTrue(config.sendingResponseMiddleware().isEmpty());
        assertTrue(config.terminatedMiddleware().isEmpty());
        assertTrue(config.callbacks().isEmpty());
    }

    @Test
    void listsAreDefensivelyCopied() {
        java.util.List<io.valkyrja.application.provider.contract.ComponentProviderContract>
                providers = new java.util.ArrayList<>();
        providers.add(new GrpcApplicationComponentProvider());

        GrpcConfig config =
                new GrpcConfig(
                        "App",
                        "dir",
                        "1.0.0",
                        "production",
                        true,
                        "UTC",
                        "key",
                        "path",
                        "ns",
                        50051,
                        providers,
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of());

        providers.clear();

        assertEquals(1, config.providers().size());
        assertTrue(config.debugMode());
    }
}
