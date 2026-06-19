/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.container.provider;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Deferred provider whose publish callback registers a real service binding (mirrors a framework
 * service provider such as DispatchServiceProvider), so resolving {@link ProvidedClass} exercises
 * the publish-then-resolve path in whichever container publishes it.
 */
public final class BindingProviderClass implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                ProvidedClass.class,
                container -> container.bind(ProvidedClass.class, (c, arguments) -> new ProvidedClass()));
    }
}
