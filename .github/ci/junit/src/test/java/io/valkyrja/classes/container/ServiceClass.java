/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.container;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;

/** Testable service whose only constructor requires the container. */
public final class ServiceClass {

    private final ContainerContract container;

    public ServiceClass(ContainerContract container) {
        this.container = container;
    }

    public static ServiceClass make(ContainerContract container, Map<String, Object> arguments) {
        return new ServiceClass(container);
    }

    public ContainerContract getContainer() {
        return container;
    }
}
