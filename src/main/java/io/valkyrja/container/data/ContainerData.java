/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.data;

import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Immutable snapshot of container bindings used for serialization and bulk import.
 *
 * @param aliases alias type → target type
 * @param callbacks service type → publish callback
 * @param services service type → factory callable
 * @param singletons service type → itself (self-map, tracks singleton registrations)
 */
public record ContainerData(
        Map<Class<?>, Class<?>> aliases,
        Map<Class<?>, Consumer<ContainerContract>> callbacks,
        Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>> services,
        Map<Class<?>, Class<?>> singletons)
        implements ContainerDataContract {

    // Compact constructor for defensive copying
    public ContainerData {
        aliases = Map.copyOf(aliases);
        callbacks = Map.copyOf(callbacks);
        services = Map.copyOf(services);
        singletons = Map.copyOf(singletons);
    }

    public ContainerData() {
        this(Map.of(), Map.of(), Map.of(), Map.of());
    }
}
