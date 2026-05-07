/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.provider;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.abstract_.ComponentProvider;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.List;

public class CliServerComponentProvider extends ComponentProvider {

    @Override
    public List<Class<? extends ServiceProviderContract>> getContainerProviders(ApplicationContract app) {
        return List.of(CliServerServiceProvider.class);
    }
}
