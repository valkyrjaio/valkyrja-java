/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.request.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.factory.RequestFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestFactory}. */
final class RequestFactoryTest {

    @Test
    void fromGlobalsWithDefaults() {
        var request = RequestFactory.fromGlobals();

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals(ProtocolVersion.V1_1, request.getProtocolVersion());
    }

    @Test
    void fromGlobalsParsesServerData() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_METHOD", "POST");
        server.put("SERVER_NAME", "example.com");
        server.put("REQUEST_URI", "/path");
        server.put("SERVER_PROTOCOL", "HTTP/1.1");

        var request = RequestFactory.fromGlobals(server, null, null, null, null);

        assertEquals(RequestMethod.POST, request.getMethod());
        assertEquals("example.com", request.getUri().getHost());
        assertTrue(request.getServerParams().getAll().containsKey("SERVER_NAME"));
    }

    @Test
    void fromGlobalsParsesCookieHeader() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("HTTP_COOKIE", "session=abc; theme=dark");

        var request = RequestFactory.fromGlobals(server, null, null, null, null);

        assertEquals("abc", request.getCookieParams().getAll().get("session"));
    }

    @Test
    void fromGlobalsRejectsInvalidProtocol() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("SERVER_PROTOCOL", "garbage");

        assertThrows(
                IllegalArgumentException.class,
                () -> RequestFactory.fromGlobals(server, null, null, null, null));
    }

    @Test
    void jsonFromGlobals() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_METHOD", "POST");

        var request =
                RequestFactory.jsonFromGlobals(server, Map.of("q", "1"), Map.of("b", "1"), null, null);

        assertInstanceOf(JsonServerRequest.class, request);
        assertEquals("1", request.getQueryParams().getAll().get("q"));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new RequestFactory() {});
    }
}