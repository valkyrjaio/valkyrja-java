/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.matcher.Matcher;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.type.data.Cast;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link Matcher}. */
final class MatcherTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private static RouteCollection collectionWith(RouteContract... routes) {
        var collection = new RouteCollection();
        for (RouteContract route : routes) {
            collection.add(route);
        }
        return collection;
    }

    @Test
    void matchesStaticPath() {
        var matcher = new Matcher(collectionWith(new Route("/users", "users.index", HANDLER)));

        assertEquals("users.index", matcher.match("/users", RequestMethod.GET).getName());
    }

    @Test
    void matchesDynamicPathAndExtractsParameters() {
        var dynamic =
                new DynamicRoute(
                        "/users/{id}",
                        "users.show",
                        "/users/(?<id>\\d+)",
                        List.of((io.valkyrja.http.routing.data.contract.ParameterContract)
                                new Parameter("id", "\\d+").withCast(new Cast("int"))),
                        HANDLER);
        var matcher = new Matcher(collectionWith(dynamic));

        var matched = (DynamicRouteContract) matcher.match("/users/42", RequestMethod.GET);

        assertEquals("42", matched.getParameter("id").getValue());
    }

    @Test
    void parameterWithoutCastUsesRawMatch() {
        var dynamic =
                new DynamicRoute(
                        "/items/{id}",
                        "items.show",
                        "/items/(?<id>\\d+)",
                        List.of((io.valkyrja.http.routing.data.contract.ParameterContract)
                                new Parameter("id", "\\d+")),
                        HANDLER);
        var matcher = new Matcher(collectionWith(dynamic));

        var matched = (DynamicRouteContract) matcher.match("/items/7", RequestMethod.GET);

        assertEquals("7", matched.getParameter("id").getValue());
    }

    @Test
    void invalidRegexIsSkipped() {
        var dynamic =
                new DynamicRoute(
                        "/bad", "bad", "[invalid", List.of(new Parameter("x", "\\d+")), HANDLER);
        var matcher = new Matcher(collectionWith(dynamic));

        assertNull(matcher.match("/other", RequestMethod.GET));
    }

    @Test
    void parameterWithoutGroupOrDefaultIsKept() {
        // The regex captures "id" but the parameter is named "other": no group match, no default.
        var dynamic =
                new DynamicRoute(
                        "/k/{x}",
                        "k",
                        "/k/(?<id>\\d+)",
                        List.of(new Parameter("other", "\\d+")),
                        HANDLER);
        var matcher = new Matcher(collectionWith(dynamic));

        var matched = (DynamicRouteContract) matcher.match("/k/9", RequestMethod.GET);

        assertNull(matched.getParameter("other").getValue());
    }

    @Test
    void returnsNullWhenNothingMatches() {
        var matcher = new Matcher(collectionWith(new Route("/users", "users.index", HANDLER)));

        assertNull(matcher.match("/missing", RequestMethod.GET));
    }

    @Test
    void matchStaticAndMatchDynamicDirectly() {
        var matcher = new Matcher(collectionWith(new Route("/a", "a", HANDLER)));

        assertTrue(matcher.matchStatic("/a", RequestMethod.GET) != null);
        assertNull(matcher.matchDynamic("/a", RequestMethod.GET));
    }

    @Test
    void noArgConstructorUsesEmptyCollection() {
        assertNull(new Matcher().match("/x", RequestMethod.GET));
    }

    @Test
    void dynamicRouteWithoutParametersThrows() {
        // The stored path key is "/users/{id}", so the request "/users/42" misses the static
        // lookup and falls through to the regex match, which then validates the parameter list.
        var dynamic =
                new DynamicRoute("/users/{id}", "x", "/users/(\\d+)", List.of(), HANDLER);
        var matcher = new Matcher(collectionWith(dynamic));

        assertThrows(
                HttpRoutingInvalidRoutePathException.class,
                () -> matcher.match("/users/42", RequestMethod.GET));
    }

    @Test
    void invalidRegexInCollectionIsSkipped() {
        var collection = mock(RouteCollectionContract.class);
        when(collection.hasPath(any(), any())).thenReturn(false);
        when(collection.getRegexes(any())).thenReturn(Map.of("/users/(", "broken"));
        var matcher = new Matcher(collection);

        assertNull(matcher.match("/users/42", RequestMethod.GET));
    }
}
