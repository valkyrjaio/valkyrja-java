/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.factory.RouteFactory;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteFactory}. */
final class RouteFactoryTest {

    private static Route route() {
        return new Route("list", "List", (c, r) -> new EmptyOutput());
    }

    @Test
    void fromRouteWithoutHelpText() {
        var copy = RouteFactory.fromRoute(route());

        assertEquals("list", copy.getName());
        assertEquals("List", copy.getDescription());
        assertFalse(copy.hasHelpText());
    }

    @Test
    void fromRouteWithHelpText() {
        var source = route().withHelpText(() -> new Message("help"));

        var copy = RouteFactory.fromRoute(source);

        assertTrue(copy.hasHelpText());
        assertEquals("help", copy.getHelpTextMessage().getText());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new RouteFactory() {});
    }
}
