/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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