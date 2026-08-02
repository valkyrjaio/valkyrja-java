/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.data.GrpcConfig;
import io.valkyrja.application.data.contract.GrpcConfigContract;
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
    void maxInboundMessagesDefaultsToOneThousand() {
        assertEquals(1000, new GrpcConfig().maxInboundMessages());
    }

    @Test
    void contractSuppliesTheDefaultInboundCapToImplementorsThatDoNotOverrideIt() {
        // An implementor that only fills in the required members inherits the contract default.
        GrpcConfigContract config = mock(GrpcConfigContract.class, CALLS_REAL_METHODS);
        assertEquals(GrpcConfigContract.DEFAULT_MAX_INBOUND_MESSAGES, config.maxInboundMessages());
    }

    @Test
    void nullMaxInboundMessagesFallsBackToTheDefault() {
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
                        null,
                        java.util.List.of(new GrpcApplicationComponentProvider()),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of(),
                        java.util.List.of());

        assertEquals(1000, config.maxInboundMessages());
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
        assertTrue(config.responseSentMiddleware().isEmpty());
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
                        1000,
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
