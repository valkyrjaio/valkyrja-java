/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.container.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

/** Test provider with two deferred publishers that flip static flags when invoked. */
public final class ProviderClass implements ServiceProviderContract {

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
                ProvidedClass.class, ProviderClass::publish,
                ProvidedSecondaryClass.class, ProviderClass::publishSecondary);
    }
}
