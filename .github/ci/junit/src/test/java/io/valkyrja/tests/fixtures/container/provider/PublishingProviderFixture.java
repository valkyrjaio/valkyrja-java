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
import java.util.Map;
import java.util.function.Consumer;

/**
 * Deferred provider that publishes one instance, and no singleton binding. The container it
 * publishes into holds the instance, so the type reads as a singleton instance and never as a
 * singleton binding.
 */
public final class PublishingProviderFixture implements ServiceProviderContract {

    public static void publishProvided(ContainerContract container) {
        container.setSingleton(ProvidedFixture.class, new ProvidedFixture());
    }

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(ProvidedFixture.class, PublishingProviderFixture::publishProvided);
    }
}
