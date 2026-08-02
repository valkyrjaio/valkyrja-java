/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.annotation.Provides;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

public class ServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(ContainerDataContract.class, ServiceProvider::publishData);
    }

    @Provides(ContainerDataContract.class)
    public static void publishData(ContainerContract container) {
        ApplicationContract app = container.getSingleton(ApplicationContract.class);

        for (var provider : app.getContainerProviders()) {
            container.register(provider);
        }

        container.setSingleton(ContainerDataContract.class, container.getData());
    }
}
