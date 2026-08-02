/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.request.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.request.factory.ServerFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ServerFactory}. */
final class ServerFactoryTest {

    @Test
    void normalizeServerReturnsServerWithAuthorization() {
        var server = Map.of("HTTP_AUTHORIZATION", "Bearer token");

        assertTrue(ServerFactory.normalizeServer(server).containsKey("HTTP_AUTHORIZATION"));
    }

    @Test
    void normalizeServerReturnsServerWithoutAuthorization() {
        var server = Map.of("SERVER_NAME", "host");

        assertTrue(ServerFactory.normalizeServer(server).containsKey("SERVER_NAME"));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new ServerFactory() {});
    }
}
