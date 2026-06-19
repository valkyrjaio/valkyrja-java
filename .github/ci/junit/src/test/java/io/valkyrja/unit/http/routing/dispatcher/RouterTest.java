/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.http.middleware.handler.RouteMatchedHandler;
import io.valkyrja.http.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.TerminatedHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.dispatcher.Router;
import io.valkyrja.http.routing.matcher.Matcher;
import io.valkyrja.http.message.uri.Uri;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link Router}. */
final class RouterTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private static Router routerFor(Matcher matcher) {
        var container = new Container();
        return new Router(
                container,
                matcher,
                new ResponseFactory(),
                new ThrowableCaughtHandler(container),
                new RouteMatchedHandler(container),
                new RouteNotMatchedHandler(container),
                new RouteDispatchedHandler(container),
                new SendingResponseHandler(container),
                new TerminatedHandler(container));
    }

    private static ServerRequestContract request(String path, RequestMethod method) {
        var request = mock(ServerRequestContract.class);
        when(request.getUri()).thenReturn(new Uri(path));
        when(request.getMethod()).thenReturn(method);
        return request;
    }

    @Test
    void noArgConstructorIsUsable() {
        assertNotNull(new Router());
    }

    @Test
    void dispatchUnmatchedReturnsNotFound() {
        var router = routerFor(new Matcher(new RouteCollection()));

        var response = router.dispatch(request("/missing", RequestMethod.GET));

        assertEquals(StatusCode.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void dispatchMatchedRunsHandler() {
        var collection = new RouteCollection();
        collection.add(new Route("/users", "users.index", HANDLER));
        var router = routerFor(new Matcher(collection));

        var response = router.dispatch(request("/users", RequestMethod.GET));

        assertNotNull(response);
    }

    @Test
    void dispatchKnownPathWrongMethodReturnsMethodNotAllowed() {
        var collection = new RouteCollection();
        collection.add(new Route("/users", "users.index", HANDLER));
        var router = routerFor(new Matcher(collection));

        var response = router.dispatch(request("/users", RequestMethod.POST));

        assertEquals(StatusCode.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void dispatchRouteDirectly() {
        var router = routerFor(new Matcher(new RouteCollection()));

        var response =
                router.dispatchRoute(
                        request("/users", RequestMethod.GET),
                        new Route("/users", "users.index", HANDLER));

        assertNotNull(response);
    }

    @Test
    void dispatchRouteShortCircuitsWhenMatchedMiddlewareReturnsResponse() {
        var container = new Container();
        var route = new Route("/users", "users.index", HANDLER);
        var shortCircuit = new EmptyResponse();
        var matchedHandler = mock(RouteMatchedHandlerContract.class);
        when(matchedHandler.routeMatched(any(), any()))
                .thenReturn(new RouteMatchedResult(route, shortCircuit));
        var router =
                new Router(
                        container,
                        new Matcher(new RouteCollection()),
                        new ResponseFactory(),
                        new ThrowableCaughtHandler(container),
                        matchedHandler,
                        new RouteNotMatchedHandler(container),
                        new RouteDispatchedHandler(container),
                        new SendingResponseHandler(container),
                        new TerminatedHandler(container));

        var response = router.dispatchRoute(request("/users", RequestMethod.GET), route);

        assertSame(shortCircuit, response);
    }
}
