/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.provider.abstract_;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.provider.abstract_.ComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link ComponentProvider} base. */
final class ComponentProviderTest {

    /** A component that contributes nothing, inheriting every default. */
    private static final class EmptyComponentProvider extends ComponentProvider {}

    private final EmptyComponentProvider provider = new EmptyComponentProvider();

    @Test
    void contributesNothingByDefault() {
        assertTrue(provider.getComponentProviders(null).isEmpty());
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
        assertTrue(provider.getGrpcProviders(null).isEmpty());
    }
}
