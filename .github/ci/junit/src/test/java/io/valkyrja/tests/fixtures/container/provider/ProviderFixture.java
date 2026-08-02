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

/** Test provider with two deferred publishers that flip static flags when invoked. */
public final class ProviderFixture implements ServiceProviderContract {

    public static boolean publishCalled = false;

    public static boolean publishSecondaryCalled = false;

    public static void publish(ContainerContract container) {
        publishCalled = true;
    }

    public static void publishSecondary(ContainerContract container) {
        publishSecondaryCalled = true;
    }

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ProvidedFixture.class, ProviderFixture::publish,
                ProvidedSecondaryFixture.class, ProviderFixture::publishSecondary);
    }
}
