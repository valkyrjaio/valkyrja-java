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
import io.valkyrja.container.provider.ContainerComponentProvider;
import io.valkyrja.event.provider.EventComponentProvider;
import org.junit.jupiter.api.Test;

final class ApplicationComponentProviderTest {

    private final ApplicationComponentProvider provider = new ApplicationComponentProvider();

    @Test
    void componentProvidersAreContainerAndEvent() {
        var components = provider.getComponentProviders(null);

        assertEquals(2, components.size());
        assertInstanceOf(ContainerComponentProvider.class, components.get(0));
        assertInstanceOf(EventComponentProvider.class, components.get(1));
    }

    @Test
    void otherProviderListsAreEmpty() {
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
    }
}
