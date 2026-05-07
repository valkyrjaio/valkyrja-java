/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.provider.contract;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.Consumer;

public interface ServiceProviderContract {

    /** Custom publish callbacks for services provided by this provider. */
    Map<Class<?>, Consumer<ContainerContract>> publishers();
}
