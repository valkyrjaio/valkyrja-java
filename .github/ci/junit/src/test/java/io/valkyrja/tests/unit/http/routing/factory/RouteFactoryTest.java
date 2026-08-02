/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
import io.valkyrja.http.routing.factory.RouteFactory;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link RouteFactory}. */
final class RouteFactoryTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    @Test
    void buildsADynamicRouteWhenThePathCarriesAParameter() {
        var built = RouteFactory.fromRoute(new Route("/users/{id}", "users.show", HANDLER));

        var dynamic = assertInstanceOf(DynamicRouteContract.class, built);
        assertEquals("/users/{id}", dynamic.getPath());
        assertEquals("users.show", dynamic.getName());
        // The regex is left for the processor to build.
        assertEquals("", dynamic.getRegex());
    }

    @Test
    void buildsAStaticRouteWhenThePathCarriesNoParameter() {
        var built = RouteFactory.fromRoute(new Route("/users", "users.index", HANDLER));

        assertFalse(built instanceof DynamicRouteContract);
        assertEquals("/users", built.getPath());
    }

    @Test
    void keepsTheParametersOfARouteThatIsAlreadyDynamic() {
        List<ParameterContract> parameters = List.of(new Parameter("id", Regex.NUM));
        var route = new DynamicRoute("/users/{id}", "users.show", "", parameters, HANDLER);

        var built = RouteFactory.fromRoute(route);

        var dynamic = assertInstanceOf(DynamicRouteContract.class, built);
        assertEquals(1, dynamic.getParameters().size());
        assertEquals("id", dynamic.getParameters().get(0).getName());
    }

    @Test
    void carriesTheRoutesRequestMethodsAndHandlerOnToTheBuiltRoute() {
        var route = new Route("/users/{id}", "users.show", HANDLER);

        var built = RouteFactory.fromRoute(route);

        assertEquals(route.getRequestMethods(), built.getRequestMethods());
        assertSame(HANDLER, built.getHandler());
    }

    @Test
    void readsNoStructsFromARouteThatDeclaresNone() {
        var route = new Route("/users", "users.index", HANDLER);

        assertNull(RouteFactory.getRequestStructFromRoute(route));
        assertNull(RouteFactory.getResponseStructFromRoute(route));
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<RouteFactory> constructor = RouteFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
