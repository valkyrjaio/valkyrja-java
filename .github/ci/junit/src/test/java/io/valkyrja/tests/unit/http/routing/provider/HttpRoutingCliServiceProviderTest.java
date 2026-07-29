/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.provider.HttpRoutingCliServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingCliServiceProvider}. */
final class HttpRoutingCliServiceProviderTest {

    @Test
    void publishersExposeListCommand() {
        assertEquals(1, new HttpRoutingCliServiceProvider().publishers().size());
    }

    @Test
    void publishListCommandBindsCommand() {
        var container = new Container();
        container.setSingleton(RouteCollectionContract.class, new RouteCollection());
        container.setSingleton(OutputFactoryContract.class, mock(OutputFactoryContract.class));

        HttpRoutingCliServiceProvider.publishListCommand(container);

        assertInstanceOf(ListCommand.class, container.getSingleton(ListCommand.class));
    }
}
