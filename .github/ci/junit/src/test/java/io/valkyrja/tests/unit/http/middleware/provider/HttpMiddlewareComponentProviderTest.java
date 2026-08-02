/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.middleware.provider.HttpMiddlewareComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMiddlewareComponentProvider}. */
final class HttpMiddlewareComponentProviderTest {

    private final HttpMiddlewareComponentProvider provider = new HttpMiddlewareComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void allProviderListsAreResolvable() {
        assertNotNull(provider.getComponentProviders(app));
        assertNotNull(provider.getContainerProviders(app));
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}
