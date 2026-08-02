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
 * Deferred provider whose publish callback registers a real service binding (mirrors a framework
 * service provider such as DispatchServiceProvider), so resolving {@link ProvidedFixture} exercises
 * the publish-then-resolve path in whichever container publishes it.
 */
public final class BindingProviderFixture implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ProvidedFixture.class,
                container ->
                        container.bind(
                                ProvidedFixture.class, (c, arguments) -> new ProvidedFixture()));
    }
}
