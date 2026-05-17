/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.factory;

import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.data.HostPortAccumulator;
import io.valkyrja.http.message.uri.enum_.Scheme;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public abstract class MarshalUriFactory {

    public static UriContract marshalUriFromServer(
            Map<String, String> server, Map<String, HeaderContract> headers) {
        UriContract uri = new Uri();

        uri = addSchemeToUri(uri, server, headers);
        uri = addHostAndPortToUri(uri, server, headers);

        return addPathQueryAndFragmentToUri(uri, server);
    }

    public static String getHeader(String headerName, Map<String, HeaderContract> headers) {
        headerName = headerName.toLowerCase(Locale.ROOT);

        HeaderContract header = null;

        for (Map.Entry<String, HeaderContract> entry : headers.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT).equals(headerName)) {
                header = entry.getValue();
                break;
            }
        }

        return header != null ? header.getHeaderLine() : "";
    }

    public static void marshalHostAndPortFromHeaders(
            HostPortAccumulator accumulator,
            Map<String, String> server,
            Map<String, HeaderContract> headers) {
        if (!getHeader("host", headers).isEmpty()) {
            marshalHostAndPortFromHeader(accumulator, getHeader("host", headers));
            return;
        }

        if (!server.containsKey("SERVER_NAME")) {
            return;
        }

        accumulator.host = server.get("SERVER_NAME");

        if (server.containsKey("SERVER_PORT")) {
            accumulator.port = Integer.parseInt(server.get("SERVER_PORT"));
        }

        if (!server.containsKey("SERVER_ADDR")
                || !accumulator.host.matches("^\\[[0-9a-fA-F:]+\\]$")) {
            return;
        }

        marshalIpv6HostAndPort(accumulator, server);
    }

    public static String marshalRequestUri(Map<String, String> server) {
        String unencodedUrl = marshalRequestUriFromUnencodedUrl(server);

        if (unencodedUrl != null) {
            return unencodedUrl;
        }

        String requestUri = marshalRequestUriFromHttpXRewriteUrl(server);

        if (requestUri == null) {
            requestUri = marshalRequestUriFromHttpXOriginalUrl(server);
        }

        if (requestUri == null) {
            requestUri = server.get("REQUEST_URI");
        }

        boolean isRequestUriValid = isValidRequestUri(requestUri);

        if (isRequestUriValid && requestUri != null) {
            String result = requestUri.replaceAll("^[^/:]+://[^/]+", "");
            return result != null ? result : requestUri;
        }

        String origPathInfo = marshalRequestUriFromOrigPathInfo(server);

        return origPathInfo != null ? origPathInfo : "/";
    }

    public static String stripQueryString(String path) {
        int queryPos = path.indexOf('?');

        if (queryPos != -1) {
            return path.substring(0, queryPos);
        }

        return path;
    }

    protected static @Nullable String marshalRequestUriFromUnencodedUrl(
            Map<String, String> server) {
        String iisUrlRewritten = server.get("IIS_WasUrlRewritten");
        String unencodedUrl = server.getOrDefault("UNENCODED_URL", "");

        if ("1".equals(iisUrlRewritten) && !unencodedUrl.isEmpty()) {
            return unencodedUrl;
        }

        return null;
    }

    protected static @Nullable String marshalRequestUriFromHttpXRewriteUrl(
            Map<String, String> server) {
        String httpXRewriteUrl = server.get("HTTP_X_REWRITE_URL");

        if (isValidRequestUri(httpXRewriteUrl)) {
            return httpXRewriteUrl;
        }

        return null;
    }

    protected static @Nullable String marshalRequestUriFromHttpXOriginalUrl(
            Map<String, String> server) {
        String httpXOriginalUrl = server.get("HTTP_X_ORIGINAL_URL");

        if (isValidRequestUri(httpXOriginalUrl)) {
            return httpXOriginalUrl;
        }

        return null;
    }

    protected static @Nullable String marshalRequestUriFromOrigPathInfo(
            Map<String, String> server) {
        String origPathInfo = server.get("ORIG_PATH_INFO");

        if (isValidRequestUri(origPathInfo)) {
            return origPathInfo;
        }

        return null;
    }

    protected static boolean isValidRequestUri(@Nullable String subject) {
        return subject != null && !subject.isEmpty();
    }

    protected static UriContract addSchemeToUri(
            UriContract uri, Map<String, String> server, Map<String, HeaderContract> headers) {
        Scheme scheme = Scheme.HTTP;

        if (shouldSchemeBeSetToHttps(server, headers)) {
            scheme = Scheme.HTTPS;
        }

        return uri.withScheme(scheme);
    }

    protected static UriContract addPathQueryAndFragmentToUri(
            UriContract uri, Map<String, String> server) {
        String path = marshalRequestUri(server);
        path = stripQueryString(path);

        String query = "";

        if (server.containsKey("QUERY_STRING")) {
            query = server.get("QUERY_STRING");
            if (query.startsWith("?")) {
                query = query.substring(1);
            }
        }

        String fragment = "";

        if (path.contains("#")) {
            int hashPos = path.indexOf('#');
            fragment = path.substring(hashPos + 1);
            path = path.substring(0, hashPos);
        }

        return uri.withPath(path).withQuery(query).withFragment(fragment);
    }

    protected static UriContract addHostAndPortToUri(
            UriContract uri, Map<String, String> server, Map<String, HeaderContract> headers) {
        HostPortAccumulator accumulator = new HostPortAccumulator();

        marshalHostAndPortFromHeaders(accumulator, server, headers);

        String host = accumulator.host;
        int port = accumulator.port;

        if (!host.isEmpty()) {
            uri = uri.withHost(host);

            if (port != 0) {
                uri = uri.withPort(port);
            }
        }

        return uri;
    }

    protected static boolean shouldSchemeBeSetToHttps(
            Map<String, String> server, Map<String, HeaderContract> headers) {
        String https = server.get("HTTPS");

        return (https != null && !https.equals("off"))
                || getHeader("x-forwarded-proto", headers).equals(Scheme.HTTPS.getValue());
    }

    protected static void marshalHostAndPortFromHeader(
            HostPortAccumulator accumulator, String host) {
        accumulator.host = host;
        accumulator.port = 0;

        Pattern pattern = Pattern.compile(":(\\d+)$");
        Matcher matcher = pattern.matcher(accumulator.host);

        if (matcher.find()) {
            String portStr = matcher.group(1);
            accumulator.host =
                    accumulator.host.substring(0, accumulator.host.length() - portStr.length() - 1);
            accumulator.port = Integer.parseInt(portStr);
        }
    }

    protected static void marshalIpv6HostAndPort(
            HostPortAccumulator accumulator, Map<String, String> server) {
        accumulator.host = "[" + server.get("SERVER_ADDR") + "]";
        accumulator.port = accumulator.port > 0 ? accumulator.port : 80;

        int portOffset = accumulator.host.lastIndexOf(':');

        if (portOffset != -1) {
            String suffix = accumulator.host.substring(portOffset + 1);
            if ((accumulator.port + "]").equals(suffix)) {
                accumulator.port = 0;
            }
        }
    }
}
