/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.data.Route;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.url.Url;
import java.util.Map;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

/** Test the {@link Url} generator. */
final class UrlTest {

    private static final BiFunction<ContainerContract, RouteContract, ResponseContract> HANDLER =
            (container, route) -> new EmptyResponse();

    @Test
    void getUrlReturnsPath() {
        var collection = new RouteCollection();
        collection.add(new Route("/users", "users.index", HANDLER));

        assertEquals("/users", new Url(collection).getUrl("users.index", Map.of()));
    }

    @Test
    void getUrlSubstitutesParameters() {
        var collection = mock(RouteCollection.class);
        when(collection.getByName("users.show"))
                .thenReturn(new Route("/users/{id}", "users.show", HANDLER));

        var url = new Url(collection).getUrl("users.show", Map.of("id", 42));

        assertEquals("/users/42", url);
    }
}