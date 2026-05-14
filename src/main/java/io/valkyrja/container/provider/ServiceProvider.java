/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
