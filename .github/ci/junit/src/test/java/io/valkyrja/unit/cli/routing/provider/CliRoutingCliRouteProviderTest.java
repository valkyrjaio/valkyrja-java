/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.routing.provider.CliRoutingCliRouteProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingCliRouteProvider}. */
final class CliRoutingCliRouteProviderTest {

    @Test
    void exposesEmptyControllersAndRoutes() {
        var provider = new CliRoutingCliRouteProvider();

        assertTrue(provider.getControllerClasses().isEmpty());
        assertTrue(provider.getRoutes().isEmpty());
    }
}
