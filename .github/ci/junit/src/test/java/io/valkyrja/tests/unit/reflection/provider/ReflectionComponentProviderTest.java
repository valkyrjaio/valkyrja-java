/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.reflection.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.reflection.provider.ReflectionComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link ReflectionComponentProvider}. */
final class ReflectionComponentProviderTest {

    private final ReflectionComponentProvider provider = new ReflectionComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void allProviderListsAreEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getContainerProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}
