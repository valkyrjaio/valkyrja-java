/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.provider.ApplicationComponentProvider;
import io.valkyrja.application.provider.HttpApplicationComponentProvider;
import io.valkyrja.log.provider.LogComponentProvider;
import org.junit.jupiter.api.Test;

final class HttpApplicationComponentProviderTest {

    private final HttpApplicationComponentProvider provider =
            new HttpApplicationComponentProvider();

    @Test
    void componentProvidersIncludeApplicationAndHttpStack() {
        var components = provider.getComponentProviders(null);

        assertEquals(7, components.size());
        assertInstanceOf(ApplicationComponentProvider.class, components.get(0));
        assertInstanceOf(LogComponentProvider.class, components.get(6));
    }

    @Test
    void otherProviderListsAreEmpty() {
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
    }
}
