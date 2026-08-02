/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.message.provider.HttpMessageComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMessageComponentProvider}. */
final class HttpMessageComponentProviderTest {

    private final HttpMessageComponentProvider provider = new HttpMessageComponentProvider();
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
