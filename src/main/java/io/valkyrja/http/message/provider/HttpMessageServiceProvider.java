/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import java.util.Map;
import java.util.function.Consumer;

public class HttpMessageServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ResponseFactoryContract.class, HttpMessageServiceProvider::publishResponseFactory);
    }

    public static void publishResponseFactory(ContainerContract container) {
        container.setSingleton(ResponseFactoryContract.class, new ResponseFactory());
    }
}
