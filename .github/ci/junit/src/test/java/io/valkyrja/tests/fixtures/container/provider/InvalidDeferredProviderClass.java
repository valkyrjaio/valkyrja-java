/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.container.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Test provider that declares a publisher with no (null) callback — an invalid deferred provider. */
public final class InvalidDeferredProviderClass implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        Map<Class<?>, Consumer<ContainerContract>> publishers = new HashMap<>();
        publishers.put(ProvidedSecondaryClass.class, null);
        return publishers;
    }
}
