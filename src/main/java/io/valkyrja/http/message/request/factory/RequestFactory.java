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
import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.factory.CookieFactory;
import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.stream.Stream;
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
        return fromGlobals(Map.of(), null, null, null, null);
    }

    public static ServerRequest fromGlobals(
            Map<String, String> server,
            @Nullable Map<String, Object> query,
            @Nullable Map<String, Object> body,
            @Nullable Map<String, String> cookies,
            @Nullable Map<String, UploadedFileContract> files) {
        server = ServerFactory.normalizeServer(server);

        Map<String, HeaderContract> headers = HeaderFactory.marshalHeaders(server);

        if (cookies == null && headers.containsKey("cookie")) {
            cookies = CookieFactory.parseCookieHeader(headers.get("cookie").getHeaderLine());
        }

        if (cookies == null) {
            cookies = Map.of();
        }

        if (files == null) {
            files = Map.of();
        }

        return new ServerRequest(
                MarshalUriFactory.marshalUriFromServer(server, headers),
                RequestMethod.from(
                        server.getOrDefault("REQUEST_METHOD", RequestMethod.GET.getValue())),
                new Stream(),
                HeaderCollection.fromArray(headers),
                getProtocolVersionFromServer(server),
                new ServerParamCollection(toStringObjectMap(server)),
                new CookieParamCollection(toStringObjectMap(cookies)),
                new QueryParamCollection(query != null ? query : Map.of()),
                new ParsedBodyParamCollection(body != null ? body : Map.of()),
                new UploadedFileCollection(files),
                null);
    }

    public static ServerRequest jsonFromGlobals(
            Map<String, String> server,
            @Nullable Map<String, Object> query,
            @Nullable Map<String, Object> body,
            @Nullable Map<String, String> cookies,
            @Nullable Map<String, UploadedFileContract> files) {
        server = ServerFactory.normalizeServer(server);

        Map<String, HeaderContract> headers = HeaderFactory.marshalHeaders(server);

        if (cookies == null && headers.containsKey("cookie")) {
            cookies = CookieFactory.parseCookieHeader(headers.get("cookie").getHeaderLine());
        }

        if (cookies == null) {
            cookies = Map.of();
        }

        if (files == null) {
            files = Map.of();
        }

        return new JsonServerRequest(
                MarshalUriFactory.marshalUriFromServer(server, headers),
                RequestMethod.from(
                        server.getOrDefault("REQUEST_METHOD", RequestMethod.GET.getValue())),
                new Stream(),
                HeaderCollection.fromArray(headers),
                getProtocolVersionFromServer(server),
                new ServerParamCollection(toStringObjectMap(server)),
                new CookieParamCollection(toStringObjectMap(cookies)),
                new QueryParamCollection(query != null ? query : Map.of()),
                new ParsedBodyParamCollection(body != null ? body : Map.of()),
                null,
                new UploadedFileCollection(files));
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
