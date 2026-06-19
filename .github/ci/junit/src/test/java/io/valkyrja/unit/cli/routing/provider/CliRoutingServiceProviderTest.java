/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ExitedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.routing.provider.CliRoutingCliRouteProvider;
import io.valkyrja.cli.routing.provider.CliRoutingServiceProvider;
import io.valkyrja.container.manager.Container;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link CliRoutingServiceProvider}. */
final class CliRoutingServiceProviderTest {

    private static Route route(String name) {
        return new Route(name, name, (c, r) -> new EmptyOutput());
    }

    @Test
    void publishersExposesRouterAndRouteCollection() {
        assertEquals(2, new CliRoutingServiceProvider().publishers().size());
    }

    @Test
    void publishRouterBindsRouter() {
        var container = new Container();
        container.setSingleton(RouteCollectionContract.class, new RouteCollection());
        container.setSingleton(OutputFactoryContract.class, mock(OutputFactoryContract.class));
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, mock(ThrowableCaughtHandlerContract.class));
        container.setSingleton(
                RouteMatchedHandlerContract.class, mock(RouteMatchedHandlerContract.class));
        container.setSingleton(
                RouteNotMatchedHandlerContract.class, mock(RouteNotMatchedHandlerContract.class));
        container.setSingleton(
                RouteDispatchedHandlerContract.class, mock(RouteDispatchedHandlerContract.class));
        container.setSingleton(ExitedHandlerContract.class, mock(ExitedHandlerContract.class));

        CliRoutingServiceProvider.publishRouter(container);

        assertInstanceOf(RouterContract.class, container.getSingleton(RouterContract.class));
    }

    @Test
    void publishRouteCollectionWithProviderRoutesOnly() {
        var container = new Container();
        var app = mock(ApplicationContract.class);
        var provider = new CliRoutingCliRouteProvider();
        when(app.getCliProviders()).thenReturn(List.of(provider));
        container.setSingleton(ApplicationContract.class, app);

        CliRoutingServiceProvider.publishRouteCollection(container);

        assertInstanceOf(
                RouteCollectionContract.class,
                container.getSingleton(RouteCollectionContract.class));
    }

    @Test
    void publishRouteCollectionUsesCollectorWhenControllersPresent() {
        var container = new Container();
        var app = mock(ApplicationContract.class);
        var provider = mock(io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract.class);
        when(provider.getControllerClasses()).thenReturn(List.of(Object.class));
        when(provider.getRoutes()).thenReturn(List.of());
        when(app.getCliProviders()).thenReturn(List.of(provider));
        container.setSingleton(ApplicationContract.class, app);
        var collector = mock(RouteCollectorContract.class);
        when(collector.getRoutes(any())).thenReturn(List.<RouteContract>of(route("generated")));
        container.setSingleton(RouteCollectorContract.class, collector);

        CliRoutingServiceProvider.publishRouteCollection(container);

        var collection = container.getSingleton(RouteCollectionContract.class);
        assertTrue(collection.has("generated"));
    }
}
