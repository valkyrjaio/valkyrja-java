/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.application.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

/** Test HTTP container-data service provider with no publishers. */
public final class HttpContainerDataProviderClass implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of();
    }
}
