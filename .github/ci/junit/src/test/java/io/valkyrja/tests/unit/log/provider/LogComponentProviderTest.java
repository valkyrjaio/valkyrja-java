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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.log.provider.LogComponentProvider;
import io.valkyrja.log.provider.LogServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link LogComponentProvider}. */
final class LogComponentProviderTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void providesTheLogServiceProvider() {
        var providers = new LogComponentProvider().getContainerProviders(app);

        assertEquals(1, providers.size());
        assertInstanceOf(LogServiceProvider.class, providers.get(0));
    }

    @Test
    void providesNothingElse() {
        var provider = new LogComponentProvider();

        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
        assertTrue(provider.getGrpcProviders(app).isEmpty());
    }
}
