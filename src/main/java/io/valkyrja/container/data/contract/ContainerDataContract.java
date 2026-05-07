/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.data.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ContainerDataContract {
    Map<Class<?>, Class<?>> aliases();

    Map<Class<?>, ServiceProviderContract> deferred();

    Map<Class<?>, Consumer<ContainerContract>> deferredCallbacks();

    Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>> services();

    Map<Class<?>, Class<?>> singletons();

    List<Class<? extends ServiceProviderContract>> providers();
}