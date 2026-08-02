/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.DynamicRoute;
import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Test the http routing {@link Processor}. */
final class ProcessorTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    private final Processor processor = new Processor();

    @Test
    void normalizesStaticPath() {
        var route = processor.route(new Route("//users//", "users", HANDLER));

        assertEquals("/users", route.getPath());
    }

    @Test
    void buildsRegexForDynamicRouteWithCapturingParameter() {
        var dynamic =
                new DynamicRoute(
                        "/{id}", "show", "", List.of(new Parameter("id", "\\d+")), HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertFalse(processed.getRegex().isEmpty());
    }

    @Test
    void buildsRegexForOptionalNonCapturingParameter() {
        var param = new Parameter("id", "\\d+", null, true, false, null, null);
        var dynamic = new DynamicRoute("/{id?}", "opt", "", List.of(param), HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertFalse(processed.getRegex().isEmpty());
    }

    @Test
    void keepsExistingRegex() {
        var dynamic =
                new DynamicRoute(
                        "/{id}",
                        "show",
                        "^/(?<id>\\d+)$",
                        List.of(new Parameter("id", "\\d+")),
                        HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertEquals("^/(?<id>\\d+)$", processed.getRegex());
    }

    @Test
    void dynamicRouteWithoutPlaceholderIsReturnedUnchanged() {
        var dynamic =
                new DynamicRoute(
                        "/static", "static", "", List.of(new Parameter("x", "\\d+")), HANDLER);

        assertEquals("/static", processor.route(dynamic).getPath());
    }

    @Test
    void throwsWhenParameterPlaceholderIsMissingFromPath() {
        var dynamic =
                new DynamicRoute(
                        "/{id}",
                        "show",
                        "",
                        List.of((ParameterContract) new Parameter("other", "\\d+")),
                        HANDLER);

        assertThrows(HttpRoutingInvalidRoutePathException.class, () -> processor.route(dynamic));
    }

    @Test
    void marksParameterOptionalWhenPlaceholderHasQuestionMark() {
        var param = new Parameter("id", "\\d+");
        var dynamic = new DynamicRoute("/{id?}", "opt", "", List.of(param), HANDLER);

        var processed = (DynamicRouteContract) processor.route(dynamic);

        assertFalse(processed.getRegex().isEmpty());
    }

    // -- Exact regex production matrices ------------------------------------------------

    static Stream<Arguments> parameterTypes() {
        return Stream.of(
                Arguments.of("num", Regex.NUM),
                Arguments.of("id", Regex.ID),
                Arguments.of("slug", Regex.SLUG),
                Arguments.of("any", Regex.ANY),
                Arguments.of("alpha", Regex.ALPHA),
                Arguments.of("alphaLowercase", Regex.ALPHA_LOWERCASE),
                Arguments.of("alphaUppercase", Regex.ALPHA_UPPERCASE),
                Arguments.of("alphaNum", Regex.ALPHA_NUM),
                Arguments.of("alphaNumUnderscore", Regex.ALPHA_NUM_UNDERSCORE),
                Arguments.of("uuid", Regex.UUID),
                Arguments.of("uuidV1", Regex.UUID_V1),
                Arguments.of("uuidV3", Regex.UUID_V3),
                Arguments.of("uuidV4", Regex.UUID_V4),
                Arguments.of("uuidV5", Regex.UUID_V5),
                Arguments.of("uuidV6", Regex.UUID_V6),
                Arguments.of("uuidV7", Regex.UUID_V7),
                Arguments.of("uuidV8", Regex.UUID_V8),
                Arguments.of("ulid", Regex.ULID),
                Arguments.of("vlid", Regex.VLID),
                Arguments.of("vlidV1", Regex.VLID_V1),
                Arguments.of("vlidV2", Regex.VLID_V2),
                Arguments.of("vlidV3", Regex.VLID_V3),
                Arguments.of("vlidV4", Regex.VLID_V4));
    }

    @ParameterizedTest(name = "type {0}")
    @MethodSource("parameterTypes")
    void capturingParameterTypeProducesExpectedRegex(String label, String typeRegex) {
        String regex = processRegex("/{value}", List.of(new Parameter("value", typeRegex)));

        assertEquals(Regex.START + Regex.PATH + "(?<value>" + typeRegex + ")" + Regex.END, regex);
    }

    @Test
    void parameterAtStartMiddleAndEndProduceExpectedRegex() {
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "(?<name>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.PATH
                        + "edit"
                        + Regex.END,
                processRegex("/{name}/edit", List.of(new Parameter("name", Regex.ALPHA))));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "user"
                        + Regex.PATH
                        + "(?<id>"
                        + Regex.NUM
                        + ")"
                        + Regex.PATH
                        + "edit"
                        + Regex.END,
                processRegex("/user/{id}/edit", List.of(new Parameter("id", Regex.NUM))));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "parameters"
                        + Regex.PATH
                        + "(?<name>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.END,
                processRegex("/parameters/{name}", List.of(new Parameter("name", Regex.ALPHA))));
    }

    @Test
    void multipleAndAdjacentParametersProduceExpectedRegex() {
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "a"
                        + Regex.PATH
                        + "(?<x>"
                        + Regex.NUM
                        + ")"
                        + Regex.PATH
                        + "b"
                        + Regex.PATH
                        + "(?<y>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.END,
                processRegex(
                        "/a/{x}/b/{y}",
                        List.of(new Parameter("x", Regex.NUM), new Parameter("y", Regex.ALPHA))));
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "(?<x>"
                        + Regex.NUM
                        + ")(?<y>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.END,
                processRegex(
                        "/{x}{y}",
                        List.of(new Parameter("x", Regex.NUM), new Parameter("y", Regex.ALPHA))));
    }

    @Test
    void modifierCombinationsProduceExpectedRegex() {
        // single optional
        assertEquals(
                Regex.START
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?<opt>"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.END,
                processRegex("/{opt?}", List.of(optional("opt", Regex.ALPHA))));
        // non-capture
        assertEquals(
                Regex.START + Regex.PATH + "(?:" + Regex.ALPHA + ")" + Regex.END,
                processRegex("/{nc}", List.of(nonCapture("nc", Regex.ALPHA))));
        // optional non-capture
        assertEquals(
                Regex.START
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?:"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.END,
                processRegex("/{onc?}", List.of(optionalNonCapture("onc", Regex.ALPHA))));
        // multiple optionals
        assertEquals(
                Regex.START
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?<a>"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?<b>"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.END,
                processRegex(
                        "/{a?}/{b?}",
                        List.of(optional("a", Regex.ALPHA), optional("b", Regex.ALPHA))));
        // mixed capture / non-capture
        assertEquals(
                Regex.START
                        + Regex.PATH
                        + "(?<cap>"
                        + Regex.ALPHA
                        + ")"
                        + Regex.PATH
                        + "(?:"
                        + Regex.NUM
                        + ")"
                        + Regex.END,
                processRegex(
                        "/{cap}/{nc}",
                        List.of(new Parameter("cap", Regex.ALPHA), nonCapture("nc", Regex.NUM))));
    }

    @Test
    void pathQuestionMarkForcesOptionalRegex() {
        // Constructed as NOT optional; the '?' in the path must still make the regex optional.
        String regex = processRegex("/{opt?}", List.of(new Parameter("opt", Regex.ALPHA)));

        assertEquals(
                Regex.START
                        + Regex.START_OPTIONAL_CAPTURE_GROUP
                        + "(?<opt>"
                        + Regex.ALPHA
                        + ")?"
                        + Regex.END,
                regex);
    }

    @Test
    void nonDynamicRouteWithBraceInPathIsLeftAsStatic() {
        var route = processor.route(new Route("/{notDynamic}", "route", HANDLER));

        assertFalse(route instanceof DynamicRouteContract);
        assertEquals("/{notDynamic}", route.getPath());
    }

    private String processRegex(String path, List<ParameterContract> parameters) {
        var route = new DynamicRoute(path, "route", "", parameters, HANDLER);
        var processed = processor.route(route);

        assertInstanceOf(DynamicRouteContract.class, processed);

        return ((DynamicRouteContract) processed).getRegex();
    }

    private static Parameter optional(String name, String regex) {
        return new Parameter(name, regex, null, true, true, null, null);
    }

    private static Parameter nonCapture(String name, String regex) {
        return new Parameter(name, regex, null, false, false, null, null);
    }

    private static Parameter optionalNonCapture(String name, String regex) {
        return new Parameter(name, regex, null, true, false, null, null);
    }
}
