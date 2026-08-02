/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.routing.provider.HttpRoutingComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingComponentProvider}. */
final class HttpRoutingComponentProviderTest {

    private final HttpRoutingComponentProvider provider = new HttpRoutingComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void exposesAllProviderLists() {
        assertNotNull(provider.getComponentProviders(app));
        assertNotNull(provider.getContainerProviders(app));
        assertNotNull(provider.getEventProviders(app));
        assertNotNull(provider.getCliProviders(app));
        assertNotNull(provider.getHttpProviders(app));
    }
}
