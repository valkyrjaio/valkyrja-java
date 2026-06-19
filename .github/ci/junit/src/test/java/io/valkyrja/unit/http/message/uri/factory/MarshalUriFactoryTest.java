/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.uri.data.HostPortAccumulator;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.uri.factory.MarshalUriFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link MarshalUriFactory}. */
final class MarshalUriFactoryTest {

    private static Map<String, HeaderContract> headers(String name, String value) {
        Map<String, HeaderContract> headers = new LinkedHashMap<>();
        if (name != null) {
            headers.put(name, new Header(name, value));
        }
        return headers;
    }

    @Test
    void getHeaderFindsCaseInsensitivelyOrReturnsEmpty() {
        var headers = headers("host", "example.com");

        assertEquals("example.com", MarshalUriFactory.getHeader("Host", headers));
        assertEquals("", MarshalUriFactory.getHeader("missing", headers));
    }

    @Test
    void stripQueryString() {
        assertEquals("/path", MarshalUriFactory.stripQueryString("/path?q=1"));
        assertEquals("/path", MarshalUriFactory.stripQueryString("/path"));
    }

    @Test
    void marshalRequestUriFromUnencodedUrl() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("IIS_WasUrlRewritten", "1");
        server.put("UNENCODED_URL", "/iis-url");

        assertEquals("/iis-url", MarshalUriFactory.marshalRequestUri(server));
    }

    @Test
    void marshalRequestUriFromRewriteAndOriginalUrls() {
        Map<String, String> rewrite = new LinkedHashMap<>();
        rewrite.put("HTTP_X_REWRITE_URL", "/rewrite");
        assertEquals("/rewrite", MarshalUriFactory.marshalRequestUri(rewrite));

        Map<String, String> original = new LinkedHashMap<>();
        original.put("HTTP_X_ORIGINAL_URL", "/original");
        assertEquals("/original", MarshalUriFactory.marshalRequestUri(original));
    }

    @Test
    void marshalRequestUriFromRequestUriStripsSchemeAndHost() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_URI", "http://example.com/path");

        assertEquals("/path", MarshalUriFactory.marshalRequestUri(server));
    }

    @Test
    void marshalRequestUriFromOrigPathInfoAndDefault() {
        Map<String, String> origPath = new LinkedHashMap<>();
        origPath.put("ORIG_PATH_INFO", "/orig");
        assertEquals("/orig", MarshalUriFactory.marshalRequestUri(origPath));

        assertEquals("/", MarshalUriFactory.marshalRequestUri(new LinkedHashMap<>()));
    }

    @Test
    void marshalsFullUriFromHostHeader() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_URI", "/path?q=1");
        server.put("QUERY_STRING", "q=1");

        var uri = MarshalUriFactory.marshalUriFromServer(server, headers("host", "example.com:8080"));

        assertEquals("example.com", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("/path", uri.getPath());
        assertEquals("q=1", uri.getQuery());
        assertEquals(Scheme.HTTP, uri.getScheme());
    }

    @Test
    void marshalsHttpsFromServerFlag() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("HTTPS", "on");
        server.put("REQUEST_URI", "/");
        server.put("SERVER_NAME", "secure.example.com");

        var uri = MarshalUriFactory.marshalUriFromServer(server, headers(null, null));

        assertEquals(Scheme.HTTPS, uri.getScheme());
        assertEquals("secure.example.com", uri.getHost());
    }

    @Test
    void marshalsHttpsFromForwardedProtoHeader() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_URI", "/");
        server.put("SERVER_NAME", "example.com");

        var uri =
                MarshalUriFactory.marshalUriFromServer(
                        server, headers("x-forwarded-proto", "https"));

        assertEquals(Scheme.HTTPS, uri.getScheme());
    }

    @Test
    void marshalsServerNameAndPort() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("SERVER_NAME", "host");
        server.put("SERVER_PORT", "9090");

        var accumulator = new HostPortAccumulator();
        MarshalUriFactory.marshalHostAndPortFromHeaders(accumulator, server, headers(null, null));

        assertEquals("host", accumulator.host);
        assertEquals(9090, accumulator.port);
    }

    @Test
    void marshalsIpv6HostFromServerAddr() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("SERVER_NAME", "[::1]");
        server.put("SERVER_ADDR", "::1");

        var accumulator = new HostPortAccumulator();
        MarshalUriFactory.marshalHostAndPortFromHeaders(accumulator, server, headers(null, null));

        assertEquals("[::1]", accumulator.host);
        assertEquals(80, accumulator.port);
    }

    @Test
    void marshalsIpv6HostWhereAddressEncodesDefaultPort() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("SERVER_NAME", "[::80]");
        server.put("SERVER_ADDR", "::80");

        var accumulator = new HostPortAccumulator();
        MarshalUriFactory.marshalHostAndPortFromHeaders(accumulator, server, headers(null, null));

        // The trailing ":80]" matches the default port, so the port is cleared back to 0.
        assertEquals(0, accumulator.port);
    }

    @Test
    void queryStringWithLeadingQuestionMarkIsStripped() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_URI", "/path");
        server.put("QUERY_STRING", "?a=1");

        var uri = MarshalUriFactory.marshalUriFromServer(server, headers("host", "example.com"));

        assertEquals("a=1", uri.getQuery());
    }

    @Test
    void marshalHostAndPortReturnsWhenNoServerName() {
        var accumulator = new HostPortAccumulator();
        MarshalUriFactory.marshalHostAndPortFromHeaders(
                accumulator, new LinkedHashMap<>(), headers(null, null));

        assertEquals("", accumulator.host);
    }

    @Test
    void fragmentInRequestUriIsExtracted() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("REQUEST_URI", "/path#section");

        var uri = MarshalUriFactory.marshalUriFromServer(server, headers("host", "example.com"));

        assertEquals("/path", uri.getPath());
        assertEquals("section", uri.getFragment());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new MarshalUriFactory() {});
    }
}
