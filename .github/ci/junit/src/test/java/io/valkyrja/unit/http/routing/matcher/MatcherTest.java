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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.matcher.Matcher;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.type.data.Cast;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @Test
    void skipsEmptyRegexAndNonMatchingPath() {
        var collection = mock(RouteCollectionContract.class);
        when(collection.hasPath(any(), any())).thenReturn(false);
        when(collection.getRegexes(any()))
                .thenReturn(Map.of("", "empty", "/nomatch/(\\d+)", "n"));

        assertNull(new Matcher(collection).match("/users/abc", RequestMethod.GET));
    }

    // -- End-to-end matching matrices (route built through the Processor) ----------------

    static Stream<Arguments> matchingTypes() {
        return Stream.of(
                Arguments.of(Regex.NUM, "123", "abc"),
                Arguments.of(Regex.ALPHA, "abc", "abc1"),
                Arguments.of(Regex.ALPHA_LOWERCASE, "abc", "Abc"),
                Arguments.of(Regex.ALPHA_UPPERCASE, "ABC", "abc"),
                Arguments.of(Regex.ALPHA_NUM, "abc123", "abc-1"),
                Arguments.of(Regex.ALPHA_NUM_UNDERSCORE, "abc_1", "abc-1"),
                Arguments.of(Regex.SLUG, "My-slug-1", "has_underscore"),
                Arguments.of(Regex.ANY, "anything", null),
                Arguments.of(Regex.UUID, "66a39476-b630-4b95-8bfb-355f3d4843c5", "not-a-uuid"),
                Arguments.of(
                        Regex.UUID_V4,
                        "78cbd961-d07b-4ef9-89a7-b4ec9d1a70f0",
                        "11111111-1111-1111-1111-111111111111"),
                Arguments.of(Regex.ULID, "01KYGBV64MKWPK63CC1QH0VGF7", "notaulid"),
                Arguments.of(Regex.VLID, "01KYGBV64MKWPK63CC1QH0VGF7", "notavlid"));
    }

    @ParameterizedTest(name = "matches {1}")
    @MethodSource("matchingTypes")
    void dynamicRouteTypeMatchesValidAndRejectsInvalid(
            String typeRegex, String valid, @Nullable String invalid) {
        var matcher =
                new Matcher(
                        collectionWith(
                                processed(
                                        "/{value}",
                                        "typed",
                                        List.of(new Parameter("value", typeRegex)))));

        var matched = (DynamicRouteContract) matcher.match("/" + valid, RequestMethod.GET);

        assertNotNull(matched);
        assertEquals(valid, matched.getParameter("value").getValue());

        if (invalid != null) {
            assertNull(matcher.match("/" + invalid, RequestMethod.GET));
        }
    }

    @Test
    void requestMethodFilteringForDynamicRoute() {
        var route =
                processed("/{name}", "get-only", List.of(new Parameter("name", Regex.ALPHA)))
                        .withRequestMethods(RequestMethod.GET);
        var matcher = new Matcher(collectionWith(route));

        assertNotNull(matcher.match("/foo", RequestMethod.GET));
        assertNull(matcher.match("/foo", RequestMethod.POST));
    }

    @Test
    void requestMethodFilteringForStaticRoute() {
        var route = new Route("/only-get", "get-only-static", HANDLER).withRequestMethods(RequestMethod.GET);
        var matcher = new Matcher(collectionWith(route));

        var matched = matcher.match("/only-get", RequestMethod.GET);

        assertNotNull(matched);
        assertFalse(matched instanceof DynamicRouteContract);
        assertNull(matcher.match("/only-get", RequestMethod.POST));
    }

    @Test
    void trailingSlashIsNormalizedForMatching() {
        var matcher =
                new Matcher(
                        collectionWith(
                                new Route("/foo", "foo-static", HANDLER),
                                processed(
                                        "/bar/{x}",
                                        "bar-dynamic",
                                        List.of(new Parameter("x", Regex.ALPHA)))));

        assertNotNull(matcher.match("/foo/", RequestMethod.GET));
        assertInstanceOf(
                DynamicRouteContract.class, matcher.match("/bar/abc/", RequestMethod.GET));
    }

    @Test
    void staticRouteTakesPrecedenceOverDynamic() {
        var matcher =
                new Matcher(
                        collectionWith(
                                new Route("/users", "static-users", HANDLER),
                                processed(
                                        "/{name}",
                                        "any-name",
                                        List.of(new Parameter("name", Regex.ALPHA)))));

        var matched = matcher.match("/users", RequestMethod.GET);

        assertNotNull(matched);
        assertFalse(matched instanceof DynamicRouteContract);
        assertInstanceOf(DynamicRouteContract.class, matcher.match("/other", RequestMethod.GET));
    }

    @Test
    void multipleParametersAreExtracted() {
        var matcher =
                new Matcher(
                        collectionWith(
                                processed(
                                        "/a/{x}/b/{y}",
                                        "multi",
                                        List.of(
                                                new Parameter("x", Regex.NUM),
                                                new Parameter("y", Regex.ALPHA)))));

        var matched = (DynamicRouteContract) matcher.match("/a/12/b/two", RequestMethod.GET);

        assertNotNull(matched);
        assertEquals("12", matched.getParameter("x").getValue());
        assertEquals("two", matched.getParameter("y").getValue());
    }

    @Test
    void nonCaptureParameterIsNotBound() {
        var param = new Parameter("nc", Regex.ALPHA, null, false, false, null, null);
        var matcher =
                new Matcher(collectionWith(processed("/{nc}", "non-capture", List.of(param))));

        var matched = (DynamicRouteContract) matcher.match("/abc", RequestMethod.GET);

        assertNotNull(matched);
        assertNull(matched.getParameter("nc").getValue());
    }

    private static DynamicRouteContract processed(
            String path, String name, List<ParameterContract> parameters) {
        var route = new DynamicRoute(path, name, "", parameters, HANDLER);

        return (DynamicRouteContract) new Processor().route(route);
    }
}
