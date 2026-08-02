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
import io.valkyrja.application.provider.CliApplicationComponentProvider;
import io.valkyrja.log.provider.LogComponentProvider;
import org.junit.jupiter.api.Test;

final class CliApplicationComponentProviderTest {

    private final CliApplicationComponentProvider provider = new CliApplicationComponentProvider();

    @Test
    void componentProvidersIncludeApplicationAndCliStack() {
        var components = provider.getComponentProviders(null);

        assertEquals(6, components.size());
        assertInstanceOf(ApplicationComponentProvider.class, components.get(0));
        assertInstanceOf(LogComponentProvider.class, components.get(5));
    }

    @Test
    void otherProviderListsAreEmpty() {
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
    }
}
