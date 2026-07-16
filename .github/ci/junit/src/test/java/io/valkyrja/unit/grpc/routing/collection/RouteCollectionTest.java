/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.routing.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.routing.collection.RouteCollection;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.throwable.exception.GrpcRoutingInvalidMethodException;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteCollection}. */
final class RouteCollectionTest {

    private static Route route(String method) {
        return new Route(method, (container, r) -> ServiceResponse.ok());
    }

    @Test
    void addAndGetByMethodKey() {
        Route route = route("/pkg.A/M");
        RouteCollection collection = new RouteCollection();
        assertSame(collection, collection.add(route));
        assertTrue(collection.has("/pkg.A/M"));
        assertSame(route, collection.get("/pkg.A/M"));
    }

    @Test
    void hasIsFalseForUnknown() {
        assertFalse(new RouteCollection().has("/pkg.A/Missing"));
    }

    @Test
    void getThrowsForUnknown() {
        assertThrows(
                GrpcRoutingInvalidMethodException.class,
                () -> new RouteCollection().get("/pkg.A/Missing"));
    }

    @Test
    void addMultipleAndAll() {
        RouteCollection collection = new RouteCollection();
        collection.add(route("/pkg.A/M1"), route("/pkg.A/M2"));
        assertEquals(2, collection.all().size());
        assertTrue(collection.all().containsKey("/pkg.A/M1"));
        assertTrue(collection.all().containsKey("/pkg.A/M2"));
    }

    @Test
    void addWithSameMethodReplaces() {
        RouteCollection collection = new RouteCollection();
        Route first = route("/pkg.A/M");
        Route second = route("/pkg.A/M");
        collection.add(first);
        collection.add(second);
        assertSame(second, collection.get("/pkg.A/M"));
        assertEquals(1, collection.all().size());
    }

    @Test
    void allReturnsCopy() {
        RouteCollection collection = new RouteCollection();
        collection.add(route("/pkg.A/M"));
        collection.all().clear();
        assertTrue(collection.has("/pkg.A/M"));
    }

    @Test
    void allIsInsertionOrdered() {
        RouteCollection collection = new RouteCollection();
        collection.add(route("/pkg.A/Z"), route("/pkg.A/A"));
        RouteContract[] values = collection.all().values().toArray(new RouteContract[0]);
        assertEquals("/pkg.A/Z", values[0].getMethod());
        assertEquals("/pkg.A/A", values[1].getMethod());
    }
}
