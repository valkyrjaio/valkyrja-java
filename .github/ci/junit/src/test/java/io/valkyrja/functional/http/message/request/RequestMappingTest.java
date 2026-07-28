/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.http.message.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.param.contract.ParamCollectionContract;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.request.factory.RequestFactory;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.enum_.Scheme;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Message-mapping fidelity for an incoming HTTP request.
 *
 * <p>Asserts that raw request inputs — the {@code $_SERVER}-style params and the raw body — land on
 * the framework's own {@link ServerRequest} object exactly as supplied, independent of routing.
 *
 * <p>Mirrors the PHP reference {@code Tests\Functional\Http\Message\Request\RequestMappingTest}.
 * Two axes differ by language rather than by intent:
 *
 * <ul>
 *   <li>PHP's SAPI hands the factory five pre-parsed globals ({@code $_SERVER}, query, body,
 *       cookies, files). The raw Java runtimes have no SAPI, so {@link RequestFactory#fromGlobals}
 *       takes the server params plus the raw body and derives the query from {@code QUERY_STRING},
 *       the cookies from the {@code Cookie} header, and the parsed body and uploaded files from the
 *       body keyed off its {@code Content-Type}.
 *   <li>PHP's param collections nest (its {@code parse_str} builds arrays from {@code a[b]=c}).
 *       Java's are flat and scalar-only, so the nesting assertions become flat-key ones.
 * </ul>
 */
final class RequestMappingTest {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    static java.util.stream.Stream<Arguments> provideProtocolVersions() {
        return java.util.stream.Stream.of(
                Arguments.of("bare 1.0", "1.0", ProtocolVersion.V1),
                Arguments.of("bare 1.1", "1.1", ProtocolVersion.V1_1),
                Arguments.of("bare 2", "2", ProtocolVersion.V2),
                Arguments.of("bare 3", "3", ProtocolVersion.V3),
                Arguments.of("prefixed 1.0", "HTTP/1.0", ProtocolVersion.V1),
                Arguments.of("prefixed 1.1", "HTTP/1.1", ProtocolVersion.V1_1),
                Arguments.of("prefixed 2", "HTTP/2", ProtocolVersion.V2),
                Arguments.of("prefixed 3", "HTTP/3", ProtocolVersion.V3));
    }

    static java.util.stream.Stream<Arguments> provideRequestTargets() {
        return java.util.stream.Stream.of(
                Arguments.of("plain path", server("REQUEST_URI", "/users/42"), "/users/42"),
                Arguments.of(
                        "path with query",
                        server("REQUEST_URI", "/users/42?page=2", "QUERY_STRING", "page=2"),
                        "/users/42?page=2"),
                Arguments.of("root path", server("REQUEST_URI", "/"), "/"),
                Arguments.of("no request uri", server(), "/"),
                Arguments.of(
                        "x rewrite url", server("HTTP_X_REWRITE_URL", "/rewritten"), "/rewritten"),
                Arguments.of(
                        "x original url", server("HTTP_X_ORIGINAL_URL", "/original"), "/original"),
                Arguments.of(
                        "unencoded url",
                        server("IIS_WasUrlRewritten", "1", "UNENCODED_URL", "/unencoded"),
                        "/unencoded"),
                Arguments.of("orig path info", server("ORIG_PATH_INFO", "/orig"), "/orig"),
                Arguments.of(
                        "absolute uri",
                        server("REQUEST_URI", "https://example.com/absolute"),
                        "/absolute"));
    }

    static java.util.stream.Stream<Arguments> provideIpv6HostsAndPorts() {
        return java.util.stream.Stream.of(
                // The address' last digit is reported as the port (as Safari on Windows does), so
                // it is reinterpreted away; the scheme's own standard port then reads back as 0.
                Arguments.of(
                        "last address digit mistaken for a port",
                        server(
                                "SERVER_NAME", "[fe80::1]",
                                "SERVER_ADDR", "fe80::1",
                                "SERVER_PORT", "1",
                                "HTTPS", "on"),
                        "[fe80::1]",
                        0),
                Arguments.of(
                        "explicit port kept",
                        server(
                                "SERVER_NAME", "[fe80::1]",
                                "SERVER_ADDR", "fe80::1",
                                "SERVER_PORT", "8080",
                                "HTTPS", "on"),
                        "[fe80::1]",
                        8080),
                Arguments.of(
                        "absent port falls back to 80",
                        server("SERVER_NAME", "[fe80::1]", "SERVER_ADDR", "fe80::1", "HTTPS", "on"),
                        "[fe80::1]",
                        80),
                Arguments.of(
                        "server name is not ipv6 shaped",
                        server(
                                "SERVER_NAME", "plain.test",
                                "SERVER_ADDR", "fe80::1",
                                "SERVER_PORT", "8080",
                                "HTTPS", "on"),
                        "plain.test",
                        8080),
                Arguments.of(
                        "ipv6 server name without an address",
                        server("SERVER_NAME", "[fe80::1]", "SERVER_PORT", "8080", "HTTPS", "on"),
                        "[fe80::1]",
                        8080));
    }

    /** Build an ordered server-param map from alternating key/value pairs. */
    private static Map<String, String> server(String... keysAndValues) {
        Map<String, String> server = new LinkedHashMap<>();

        for (int i = 0; i < keysAndValues.length; i += 2) {
            server.put(keysAndValues[i], keysAndValues[i + 1]);
        }

        return server;
    }

    private static Stream streamOf(String contents) {
        Stream stream = new Stream();
        stream.write(contents);
        stream.rewind();

        return stream;
    }

    /** Every request method spelled in the server params maps onto the enum case. */
    @ParameterizedTest
    @EnumSource(RequestMethod.class)
    void testRequestMethodMapsFromServer(RequestMethod method) {
        ServerRequest request =
                RequestFactory.fromGlobals(server("REQUEST_METHOD", method.getValue()), null);

        assertEquals(method, request.getMethod());
        assertEquals(method.getValue(), request.getMethod().getValue());
    }

    /** The method survives a withMethod() round-trip without touching the original. */
    @ParameterizedTest
    @EnumSource(RequestMethod.class)
    void testRequestMethodRoundTrips(RequestMethod method) {
        ServerRequest request = new ServerRequest();
        var updated = request.withMethod(method);

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals(method, updated.getMethod());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideProtocolVersions")
    void testProtocolVersionMapsFromServer(
            String name, String serverProtocol, ProtocolVersion expected) {
        ServerRequest request =
                RequestFactory.fromGlobals(server("SERVER_PROTOCOL", serverProtocol), null);

        assertEquals(expected, request.getProtocolVersion());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideRequestTargets")
    void testRequestTargetMapsFromServer(
            String name, Map<String, String> serverParams, String expected) {
        ServerRequest request = RequestFactory.fromGlobals(serverParams, null);

        assertEquals(expected, request.getRequestTarget());
    }

    /** A fully populated server-param map maps onto every URI component. */
    @Test
    void testUriMapsFromServer() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "REQUEST_METHOD", RequestMethod.POST.getValue(),
                                "REQUEST_URI", "/users/42/edit?page=2&sort=name",
                                "QUERY_STRING", "page=2&sort=name",
                                "HTTPS", "on",
                                "HTTP_HOST", "example.com:8443"),
                        null);

        UriContract uri = request.getUri();

        assertEquals(Scheme.HTTPS, uri.getScheme());
        assertTrue(uri.isSecure());
        assertEquals("example.com", uri.getHost());
        assertEquals(8443, uri.getPort());
        assertEquals("/users/42/edit", uri.getPath());
        assertEquals("page=2&sort=name", uri.getQuery());
        assertEquals("", uri.getFragment());
        assertEquals("https://example.com:8443/users/42/edit?page=2&sort=name", uri.toString());
        assertEquals("/users/42/edit?page=2&sort=name", request.getRequestTarget());
    }

    /**
     * The scheme falls back to http, and the host comes from SERVER_NAME/SERVER_PORT when no Host
     * header is present.
     */
    @Test
    void testUriFallsBackToServerNameAndPort() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "SERVER_NAME", "internal.test",
                                "SERVER_PORT", "8080",
                                "REQUEST_URI", "/health"),
                        null);

        UriContract uri = request.getUri();

        assertEquals(Scheme.HTTP, uri.getScheme());
        assertFalse(uri.isSecure());
        assertEquals("internal.test", uri.getHost());
        assertEquals(8080, uri.getPort());
    }

    /** A fragment is carried through when the request target has no query string. */
    @Test
    void testUriFragmentMapsFromServer() {
        ServerRequest request =
                RequestFactory.fromGlobals(server("REQUEST_URI", "/docs#section"), null);

        assertEquals("/docs", request.getUri().getPath());
        assertEquals("section", request.getUri().getFragment());
    }

    /**
     * A SERVER_NAME that looks like a bracketed IPv6 address is re-derived from SERVER_ADDR, and
     * the port is reinterpreted when the address' last digit was mistaken for one.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideIpv6HostsAndPorts")
    void testIpv6HostAndPortMapFromServer(
            String name, Map<String, String> serverParams, String expectedHost, int expectedPort) {
        UriContract uri = RequestFactory.fromGlobals(serverParams, null).getUri();

        assertEquals(expectedHost, uri.getHost());
        assertEquals(expectedPort, uri.getPort());
    }

    /** The X-Forwarded-Proto header promotes the scheme to https. */
    @Test
    void testUriSchemeMapsFromForwardedProtoHeader() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "HTTP_X_FORWARDED_PROTO",
                                Scheme.HTTPS.getValue(),
                                "HTTP_HOST",
                                "example.com"),
                        null);

        assertEquals(Scheme.HTTPS, request.getUri().getScheme());
    }

    /** Query params are decoded out of QUERY_STRING onto the query collection verbatim. */
    @Test
    void testQueryParamsMapFromGlobals() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "QUERY_STRING",
                                "page=2&sort=name&filter=active&q=hello+world&path=%2Fusers%2F42"),
                        null);

        ParamCollectionContract query = request.getQueryParams();

        assertTrue(query.has("page"));
        assertEquals("2", query.get("page"));
        assertEquals("name", query.get("sort"));
        assertEquals("active", query.get("filter"));

        // Values are percent- and plus-decoded.
        assertEquals("hello world", query.get("q"));
        assertEquals("/users/42", query.get("path"));

        // A missing query param reads back as an empty string, never null.
        assertEquals("", query.get("missing"));
        assertFalse(query.has("missing"));
    }

    /** A form-encoded body maps onto the parsed-body collection verbatim. */
    @Test
    void testParsedBodyMapsFromGlobals() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server("CONTENT_TYPE", FORM_CONTENT_TYPE), "title=hello&count=3&note=a+b");

        ParamCollectionContract parsedBody = request.getParsedBody();

        assertTrue(parsedBody.has("title"));
        assertEquals("hello", parsedBody.get("title"));
        assertEquals("3", parsedBody.get("count"));
        assertEquals("a b", parsedBody.get("note"));

        // The fields keep the order they were spelled in the body.
        assertEquals(List.of("title", "count", "note"), List.copyOf(parsedBody.getAll().keySet()));

        // A missing parsed-body param reads back as an empty string, never null.
        assertEquals("", parsedBody.get("missing"));
        assertFalse(parsedBody.has("missing"));

        // The raw body is still readable verbatim off the body stream.
        assertEquals("title=hello&count=3&note=a+b", request.getBody().getContents());
    }

    /** A body whose content type is not form-encoded yields no parsed fields. */
    @Test
    void testNonFormBodyYieldsNoParsedParams() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server("CONTENT_TYPE", "application/json"), "{\"title\":\"hello\"}");

        assertEquals(Map.of(), request.getParsedBody().getAll());
        assertEquals(Map.of(), request.getUploadedFiles().getAll());
        assertEquals("{\"title\":\"hello\"}", request.getBody().getContents());
    }

    /** Server params are exposed exactly as supplied. */
    @Test
    void testServerParamsMapFromGlobals() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "REQUEST_METHOD", RequestMethod.PUT.getValue(),
                                "REQUEST_URI", "/resource",
                                "SERVER_PROTOCOL", "HTTP/1.1"),
                        null);

        ParamCollectionContract serverParams = request.getServerParams();

        assertEquals(RequestMethod.PUT.getValue(), serverParams.get("REQUEST_METHOD"));
        assertEquals("/resource", serverParams.get("REQUEST_URI"));
        assertEquals("HTTP/1.1", serverParams.get("SERVER_PROTOCOL"));
    }

    /**
     * HTTP_* and CONTENT_* server entries become normalized headers, and lookups are
     * case-insensitive.
     */
    @Test
    void testHeadersMapFromServerCaseInsensitively() {
        ServerRequest request =
                RequestFactory.fromGlobals(
                        server(
                                "HTTP_HOST", "example.com",
                                "HTTP_ACCEPT", "text/html",
                                "HTTP_X_CUSTOM_KEY", "custom-value",
                                "CONTENT_TYPE", "application/json",
                                "CONTENT_LENGTH", "42",
                                "NOT_A_HEADER", "ignored"),
                        null);

        var headers = request.getHeaders();

        // Header names are normalized to lower case, in the order they appear in the server params.
        assertEquals(
                List.of("host", "accept", "x-custom-key", "content-type", "content-length"),
                List.copyOf(headers.getAll().keySet()));

        assertTrue(headers.has("Accept"));
        assertTrue(headers.has("ACCEPT"));
        assertTrue(headers.has("accept"));
        assertEquals("text/html", headers.getHeaderLine("AcCePt"));
        assertEquals("custom-value", headers.getHeaderLine("X-Custom-Key"));
        assertEquals("application/json", headers.getHeaderLine("Content-Type"));
        assertEquals("42", headers.getHeaderLine("CONTENT-LENGTH"));

        assertFalse(headers.has("Not-A-Header"));
        assertEquals("", headers.getHeaderLine("Not-A-Header"));
    }

    /** A multi-value header exposes each value and joins them for the header line. */
    @Test
    void testMultiValueHeadersMapOntoRequest() {
        ServerRequestContract request =
                (ServerRequestContract)
                        new ServerRequest()
                                .withHeaders(
                                        new HeaderCollection(
                                                new Header(
                                                        "Accept",
                                                        "text/html",
                                                        "application/xhtml+xml"),
                                                new Header("X-Trace", "first")));

        HeaderContract accept = request.getHeaders().get("accept");

        assertEquals("Accept", accept.getName());
        assertEquals("accept", accept.getNormalizedName());
        assertEquals(List.of("text/html", "application/xhtml+xml"), accept.getValues());
        assertEquals("text/html, application/xhtml+xml", accept.getHeaderLine());
        assertEquals(2, accept.count());

        var added =
                request.withHeaders(
                        request.getHeaders()
                                .withAddedHeaders(new Header("ACCEPT", "application/json")));

        assertEquals(
                "text/html, application/xhtml+xml, application/json",
                added.getHeaders().getHeaderLine("accept"));

        var overridden =
                request.withHeaders(
                        request.getHeaders().withHeader(new Header("ACCEPT", "text/plain")));

        assertEquals("text/plain", overridden.getHeaders().getHeaderLine("Accept"));
        assertEquals("first", overridden.getHeaders().getHeaderLine("x-trace"));
    }

    /** A comma-delimited raw header line splits into discrete values. */
    @Test
    void testHeaderFromRawValueSplitsOnCommas() {
        HeaderContract header = Header.fromValue("X-Multi: a,b,c");

        assertEquals("X-Multi", header.getName());
        assertEquals("x-multi", header.getNormalizedName());
        assertEquals("a, b, c", header.getHeaderLine());
        assertEquals("X-Multi: a, b, c", header.toString());
    }

    /** Cookies are parsed out of the Cookie header. */
    @Test
    void testCookiesMapFromCookieHeader() {
        ServerRequest request =
                RequestFactory.fromGlobals(server("HTTP_COOKIE", "sid=abc123; theme=dark"), null);

        ParamCollectionContract cookies = request.getCookieParams();

        assertEquals(Map.of("sid", "abc123", "theme", "dark"), cookies.getAll());
        assertEquals("abc123", cookies.get("sid"));
        assertEquals("dark", cookies.get("theme"));
        assertEquals("sid=abc123; theme=dark", request.getHeaders().getHeaderLine("Cookie"));
    }

    /** A single uploaded file maps out of a multipart body onto an uploaded-file object. */
    @Test
    void testUploadedFilesMapFromMultipartBody() {
        String boundary = "----ValkyrjaBoundary";
        String body =
                "--"
                        + boundary
                        + "\r\n"
                        + "Content-Disposition: form-data; name=\"avatar\";"
                        + " filename=\"avatar.png\"\r\n"
                        + "Content-Type: image/png\r\n"
                        + "\r\n"
                        + "binary-content\r\n"
                        + "--"
                        + boundary
                        + "--\r\n";

        ServerRequest request =
                RequestFactory.fromGlobals(
                        server("CONTENT_TYPE", "multipart/form-data; boundary=" + boundary), body);

        UploadedFileContract file = request.getUploadedFiles().get("avatar");

        assertEquals("avatar.png", file.getClientFilename());
        assertEquals("image/png", file.getClientMediaType());
        assertEquals("binary-content".length(), file.getSize());
        assertTrue(file.hasSize());
        assertTrue(file.hasClientFilename());
        assertTrue(file.hasClientMediaType());
        assertEquals("binary-content", file.getStream().getContents());
    }

    /**
     * A multipart body carrying several parts maps each file onto the uploaded-file collection and
     * each plain part onto the parsed body.
     */
    @Test
    void testMultipleUploadedFilesAndFieldsMapFromMultipartBody() {
        String boundary = "----ValkyrjaBoundary";
        String body =
                "--"
                        + boundary
                        + "\r\n"
                        + "Content-Disposition: form-data; name=\"first\";"
                        + " filename=\"one.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "one\r\n"
                        + "--"
                        + boundary
                        + "\r\n"
                        + "Content-Disposition: form-data; name=\"second\";"
                        + " filename=\"two.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "second body\r\n"
                        + "--"
                        + boundary
                        + "\r\n"
                        + "Content-Disposition: form-data; name=\"title\"\r\n"
                        + "\r\n"
                        + "hello\r\n"
                        + "--"
                        + boundary
                        + "--\r\n";

        ServerRequest request =
                RequestFactory.fromGlobals(
                        server("CONTENT_TYPE", "multipart/form-data; boundary=" + boundary), body);

        var files = request.getUploadedFiles();

        assertEquals(List.of("first", "second"), List.copyOf(files.getAll().keySet()));

        UploadedFileContract first = files.get("first");
        UploadedFileContract second = files.get("second");

        assertEquals("one.txt", first.getClientFilename());
        assertEquals("text/plain", first.getClientMediaType());
        assertEquals(3, first.getSize());
        assertEquals("one", first.getStream().getContents());

        assertEquals("two.txt", second.getClientFilename());
        assertEquals(11, second.getSize());
        assertEquals("second body", second.getStream().getContents());

        // The non-file part lands on the parsed body instead.
        assertEquals("hello", request.getParsedBody().get("title"));
        assertFalse(request.getUploadedFiles().has("title"));
    }

    /**
     * Swapping the URI re-derives the Host header from it — unless the caller asks to preserve an
     * existing Host header, or the new URI carries no host at all.
     */
    @Test
    void testWithUriReDerivesTheHostHeader() {
        ServerRequestContract request =
                (ServerRequestContract)
                        new ServerRequest()
                                .withHeaders(
                                        new HeaderCollection(
                                                new Header(HeaderName.HOST, "original.test")));
        request = (ServerRequestContract) request.withUri(uriWithHost("original.test", 0), false);
        // Re-assert the starting point: swapping in the same host leaves the header as it was.
        assertEquals("original.test", request.getHeaders().getHeaderLine(HeaderName.HOST));

        // A host without a port yields a bare host header.
        var swapped = request.withUri(uriWithHost("new.test", 0), false);

        assertEquals("new.test", swapped.getHeaders().getHeaderLine(HeaderName.HOST));

        // A host with a port yields host:port.
        var withPort = request.withUri(uriWithHost("new.test", 9090), false);

        assertEquals("new.test:9090", withPort.getHeaders().getHeaderLine(HeaderName.HOST));

        // preserveHost keeps the existing header even though the URI changed.
        var preserved = request.withUri(uriWithHost("new.test", 0), true);

        assertEquals("original.test", preserved.getHeaders().getHeaderLine(HeaderName.HOST));
        assertEquals("new.test", preserved.getUri().getHost());

        // A URI with no host leaves the existing header alone.
        var hostless = request.withUri(new Uri(), false);

        assertEquals("original.test", hostless.getHeaders().getHeaderLine(HeaderName.HOST));
        assertEquals("", hostless.getUri().getHost());

        // preserveHost with no header to preserve still derives one from the URI.
        var noHeader = new ServerRequest().withUri(uriWithHost("new.test", 8080), true);

        assertEquals("new.test:8080", noHeader.getHeaders().getHeaderLine(HeaderName.HOST));

        // The original is untouched throughout.
        assertEquals("original.test", request.getHeaders().getHeaderLine(HeaderName.HOST));
    }

    private static UriContract uriWithHost(String host, int port) {
        return new Uri(Scheme.EMPTY, "", "", host, port, "", "", "");
    }

    /** The body stream is exposed verbatim and rewinds for repeat reads. */
    @Test
    void testBodyContentsMapOntoRequest() {
        var request = new ServerRequest().withBody(streamOf("{\"title\":\"hello\"}"));

        assertEquals("{\"title\":\"hello\"}", request.getBody().getContents());
        assertEquals("{\"title\":\"hello\"}", request.getBody().toString());

        var updated = request.withBody(streamOf("replaced"));

        assertEquals("replaced", updated.getBody().toString());
        assertEquals("{\"title\":\"hello\"}", request.getBody().toString());
    }

    /** Empty server params and no body yield the documented defaults. */
    @Test
    void testDefaultsWhenNothingIsSupplied() {
        ServerRequest request = RequestFactory.fromGlobals(Map.of(), null);

        assertEquals(RequestMethod.GET, request.getMethod());
        assertEquals(ProtocolVersion.V1_1, request.getProtocolVersion());
        assertEquals("/", request.getRequestTarget());
        assertEquals("", request.getUri().getHost());
        assertEquals(Map.of(), request.getQueryParams().getAll());
        assertEquals(Map.of(), request.getParsedBody().getAll());
        assertEquals(Map.of(), request.getCookieParams().getAll());
        assertEquals(Map.of(), request.getUploadedFiles().getAll());
        assertEquals(Map.of(), request.getAttributes().getAll());
        assertEquals("", request.getBody().getContents());
    }

    /** The X-Requested-With header drives the XHR flag. */
    @Test
    void testXmlHttpRequestFlagMapsFromHeader() {
        ServerRequest xhr =
                RequestFactory.fromGlobals(server("HTTP_X_REQUESTED_WITH", "XMLHttpRequest"), null);
        ServerRequest plain = RequestFactory.fromGlobals(Map.of(), null);

        assertTrue(xhr.isXmlHttpRequest());
        assertFalse(plain.isXmlHttpRequest());
    }
}
