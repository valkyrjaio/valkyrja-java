/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.response.factory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.response.contract.TextResponseContract;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ResponseFactory}. */
final class ResponseFactoryTest {

    private final ResponseFactory factory = new ResponseFactory();

    @Test
    void createsResponseTypes() {
        assertInstanceOf(ResponseContract.class, factory.createResponse("c", null, null));
        assertInstanceOf(TextResponseContract.class, factory.createTextResponse("c", null, null));
        assertInstanceOf(
                JsonResponseContract.class,
                factory.createJsonResponse(Map.of("a", "b"), null, null));
        assertInstanceOf(
                RedirectResponseContract.class,
                factory.createRedirectResponse("/here", null, null));
    }

    @Test
    void createsJsonpResponse() {
        var jsonp = factory.createJsonpResponse("cb", Map.of("a", "b"), null, null);

        assertTrue(jsonp.getBody().toString().startsWith("/**/cb("));
    }

    @Test
    void createRedirectResponseDefaultsToRoot() {
        assertInstanceOf(
                RedirectResponseContract.class, factory.createRedirectResponse(null, null, null));
    }
}
