/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.container.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Test provider that declares a publisher with no (null) callback — an invalid deferred provider.
 */
public final class InvalidDeferredProviderFixture implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        Map<Class<?>, Consumer<ContainerContract>> publishers = new HashMap<>();
        publishers.put(ProvidedSecondaryFixture.class, null);
        return publishers;
    }
}
