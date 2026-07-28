/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.factory;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.factory.CookieFactory;
import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.data.ParsedRequestBody;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.factory.MarshalUriFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public abstract class RequestFactory {

    private static final Pattern PROTOCOL_PATTERN =
            Pattern.compile("^(HTTP/)?(?<version>[1-9]\\d*(?:\\.\\d)?)$");

    public static ServerRequest fromGlobals() {
        return fromGlobals(Map.of(), null);
    }

    /**
     * Build a server request from the runtime's server params and raw body.
     *
     * <p>Everything else is derived from those two inputs: the query params from the {@code
     * QUERY_STRING} server param, the cookies from the {@code Cookie} header, and the parsed body
     * and uploaded files from the raw body keyed off its {@code Content-Type} (see {@link
     * RequestBodyFactory}). The raw body is also set as the request's body stream.
     *
     * <p>The raw runtimes have no SAPI to pre-parse {@code $_POST} / {@code $_FILES}, so unlike PHP
     * this factory derives them here rather than receiving them pre-parsed.
     *
     * @param server the {@code $_SERVER}-style params
     * @param body the raw request body, or {@code null}
     * @return the server request
     */
    public static ServerRequest fromGlobals(Map<String, String> server, @Nullable String body) {
        return build(server, body, false);
    }

    /**
     * Build a JSON server request; identical to {@link #fromGlobals(Map, String)} except the
     * returned request also parses a JSON body.
     *
     * @param server the {@code $_SERVER}-style params
     * @param body the raw request body, or {@code null}
     * @return the JSON server request
     */
    public static ServerRequest jsonFromGlobals(Map<String, String> server, @Nullable String body) {
        return build(server, body, true);
    }

    private static ServerRequest build(
            Map<String, String> server, @Nullable String body, boolean json) {
        server = ServerFactory.normalizeServer(server);

        Map<String, HeaderContract> headers = HeaderFactory.marshalHeaders(server);

        Map<String, String> cookies =
                headers.containsKey("cookie")
                        ? CookieFactory.parseCookieHeader(headers.get("cookie").getHeaderLine())
                        : Map.of();

        Map<String, Object> query = RequestBodyFactory.parseUrlEncoded(server.get("QUERY_STRING"));

        String contentType =
                headers.containsKey("content-type")
                        ? headers.get("content-type").getHeaderLine()
                        : "";
        String rawBody = body != null ? body : "";
        ParsedRequestBody parsedBody = RequestBodyFactory.parse(contentType, rawBody);

        Stream stream = new Stream();
        stream.write(rawBody);
        stream.rewind();

        UriContract uri = MarshalUriFactory.marshalUriFromServer(server, headers);
        RequestMethod method =
                RequestMethod.from(
                        server.getOrDefault("REQUEST_METHOD", RequestMethod.GET.getValue()));
        ProtocolVersion protocol = getProtocolVersionFromServer(server);
        HeaderCollectionContract headerCollection = HeaderCollection.fromArray(headers);
        ServerParamCollection serverParams = new ServerParamCollection(toStringObjectMap(server));
        CookieParamCollection cookieParams = new CookieParamCollection(toStringObjectMap(cookies));
        QueryParamCollection queryParams = new QueryParamCollection(query);
        ParsedBodyParamCollection parsedBodyParams =
                new ParsedBodyParamCollection(parsedBody.parsedBody());
        UploadedFileCollection files = new UploadedFileCollection(parsedBody.files());

        if (json) {
            return new JsonServerRequest(
                    uri,
                    method,
                    stream,
                    headerCollection,
                    protocol,
                    serverParams,
                    cookieParams,
                    queryParams,
                    parsedBodyParams,
                    null,
                    files);
        }

        return new ServerRequest(
                uri,
                method,
                stream,
                headerCollection,
                protocol,
                serverParams,
                cookieParams,
                queryParams,
                parsedBodyParams,
                files,
                null);
    }

    protected static ProtocolVersion getProtocolVersionFromServer(Map<String, String> server) {
        String serverProtocol = server.get("SERVER_PROTOCOL");

        if (serverProtocol == null) {
            return ProtocolVersion.V1_1;
        }

        Matcher matcher = PROTOCOL_PATTERN.matcher(serverProtocol);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Unrecognized protocol version (" + serverProtocol + ")");
        }

        return ProtocolVersion.from(matcher.group("version"));
    }

    private static Map<String, Object> toStringObjectMap(Map<String, String> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
