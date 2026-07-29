/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidRouteNameException;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteCollection}. */
final class RouteCollectionTest {

    private static Route route(String name) {
        return new Route(name, name + " description", (c, r) -> new EmptyOutput());
    }

    @Test
    void addAndGet() {
        var collection = new RouteCollection();

        var returned = collection.add(route("list"), route("help"));

        assertSame(collection, returned);
        assertTrue(collection.has("list"));
        assertEquals("list", collection.get("list").getName());
        assertEquals(2, collection.all().size());
    }

    @Test
    void getThrowsForUnknownRoute() {
        assertThrows(
                CliRoutingInvalidRouteNameException.class, () -> new RouteCollection().get("nope"));
    }

    @Test
    void hasFalseForUnknownRoute() {
        assertFalse(new RouteCollection().has("nope"));
    }
}
