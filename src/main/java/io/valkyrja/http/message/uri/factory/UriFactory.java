/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.factory;

import io.valkyrja.http.message.constant.Port;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidFromStringException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPathException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;
import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidQueryException;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class UriFactory {

    public static UriContract fromString(String uri) {
        if (!uri.isEmpty()
                && !uri.startsWith("/")
                && !uri.startsWith(Scheme.HTTP.getValue())
                && !uri.startsWith(Scheme.HTTPS.getValue())) {
            uri = "//" + uri;
        }

        try {
            URI parsed = new URI(uri);

            String scheme   = parsed.getScheme() != null ? parsed.getScheme() : "";
            String userInfo = parsed.getRawUserInfo() != null ? parsed.getRawUserInfo() : "";
            String host     = parsed.getHost() != null ? parsed.getHost() : "";
            int    port     = parsed.getPort() != -1 ? parsed.getPort() : 0;
            String path     = parsed.getRawPath() != null ? parsed.getRawPath() : "";
            String query    = parsed.getRawQuery() != null ? parsed.getRawQuery() : "";
            String fragment = parsed.getRawFragment() != null ? parsed.getRawFragment() : "";

            String username = "";
            String password = "";

            if (!userInfo.isEmpty()) {
                int colon = userInfo.indexOf(':');
                if (colon != -1) {
                    username = userInfo.substring(0, colon);
                    password = userInfo.substring(colon + 1);
                } else {
                    username = userInfo;
                }
            }

            return new Uri(
                filterScheme(scheme),
                username,
                password,
                host,
                port,
                path,
                query,
                fragment
            );
        } catch (URISyntaxException e) {
            throw new HttpUriInvalidFromStringException("Invalid uri `" + uri + "` provided", e);
        }
    }

    public static String toString(UriContract uri) {
        return getSchemeStringPart(uri)
            + getAuthorityStringPart(uri)
            + getPathStringPart(uri)
            + getQueryStringPart(uri)
            + getFragmentStringPart(uri);
    }

    public static Scheme filterScheme(String scheme) {
        scheme = scheme.toLowerCase();
        scheme = scheme.replaceAll(":(//)?$", "");

        for (Scheme s : Scheme.values()) {
            if (s.getValue().equals(scheme)) {
                return s;
            }
        }

        return Scheme.EMPTY;
    }

    public static void validatePort(int port) {
        if (!Port.isValid(port)) {
            throw new HttpUriInvalidPortException("Invalid port `%" + port + "` specified; must be a valid TCP/UDP port");
        }
    }

    public static String filterUserInfo(String userInfo) {
        return userInfo;
    }

    public static String filterPath(String path) {
        validatePath(path);

        if (path.startsWith("/")) {
            return "/" + path.replaceAll("^/+", "");
        }

        return path;
    }

    public static void validatePath(String path) {
        if (path.contains("?")) {
            throw new HttpUriInvalidPathException("Invalid path of `" + path + "` provided; must not contain a query string");
        }

        if (path.contains("#")) {
            throw new HttpUriInvalidPathException("Invalid path of `" + path + "` provided; must not contain a URI fragment");
        }
    }

    public static String filterQuery(String query) {
        validateQuery(query);

        if (query.startsWith("?")) {
            return query.substring(1);
        }

        return query;
    }

    public static void validateQuery(String query) {
        if (query.contains("#")) {
            throw new HttpUriInvalidQueryException(
                "Invalid query string of `" + query + "` provided; must not contain a URI fragment"
            );
        }
    }

    public static String filterFragment(String fragment) {
        validateFragment(fragment);

        if (fragment.startsWith("#")) {
            return fragment.substring(1);
        }

        return fragment;
    }

    public static void validateFragment(String fragment) {
    }

    public static boolean isStandardPort(Scheme scheme, String host, int port) {
        if (scheme == Scheme.EMPTY) {
            return !host.isEmpty() && port <= 0;
        }

        if (host.isEmpty() || port <= 0) {
            return true;
        }

        return isStandardUnsecurePort(scheme, port) || isStandardSecurePort(scheme, port);
    }

    public static boolean isStandardUnsecurePort(Scheme scheme, int port) {
        return scheme == Scheme.HTTP && port == Port.HTTP;
    }

    public static boolean isStandardSecurePort(Scheme scheme, int port) {
        return scheme == Scheme.HTTPS && port == Port.HTTPS;
    }

    public static String getSchemeStringPart(UriContract uri) {
        Scheme scheme = uri.getScheme();

        if (scheme != Scheme.EMPTY) {
            return scheme.getValue() + ":";
        }

        return "";
    }

    public static String getAuthorityStringPart(UriContract uri) {
        String authority = uri.getAuthority();

        if (!authority.isEmpty()) {
            return "//" + authority;
        }

        return "";
    }

    public static String getPathStringPart(UriContract uri) {
        String path = uri.getPath();

        if (!path.isEmpty()) {
            if (path.charAt(0) != '/') {
                path = "/" + path;
            }

            return path;
        }

        return "";
    }

    public static String getQueryStringPart(UriContract uri) {
        String query = uri.getQuery();

        if (!query.isEmpty()) {
            return "?" + query;
        }

        return "";
    }

    public static String getFragmentStringPart(UriContract uri) {
        String fragment = uri.getFragment();

        if (!fragment.isEmpty()) {
            return "#" + fragment;
        }

        return "";
    }
}
