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
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UriFactory {

    /**
     * The unreserved characters, which every uri component allows unencoded.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.3">RFC 3986 section 2.3</a>
     */
    private static final String CHAR_UNRESERVED = "a-zA-Z0-9_\\-\\.~";

    /**
     * The sub-delimiters, which every uri component this factory filters allows unencoded.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986 section 2.2</a>
     */
    private static final String CHAR_SUB_DELIMS = "!\\$&'\\(\\)\\*\\+,;=";

    /** The user info also allows the colon that separates the username from the password. */
    private static final Pattern USER_INFO_PATTERN = encodePattern(":");

    /** A reg-name allows no character beyond the unreserved characters and the sub-delimiters. */
    private static final Pattern HOST_PATTERN = encodePattern("");

    /** The path also allows a colon, an at sign, and the segment separator. */
    private static final Pattern PATH_PATTERN = encodePattern(":@/");

    /** The query and the fragment also allow a colon, an at sign, a slash, and a question mark. */
    private static final Pattern QUERY_PATTERN = encodePattern(":@/?");

    public static UriContract fromString(String uri) {
        // A value starting with "https" already starts with "http", so the http check covers both.
        if (!uri.isEmpty() && !uri.startsWith("/") && !uri.startsWith(Scheme.HTTP.getValue())) {
            uri = "//" + uri;
        }

        try {
            URI parsed = new URI(uri);

            String scheme = parsed.getScheme() != null ? parsed.getScheme() : "";
            String userInfo = parsed.getRawUserInfo() != null ? parsed.getRawUserInfo() : "";
            String host = parsed.getHost() != null ? parsed.getHost() : "";
            int port = parsed.getPort() != -1 ? parsed.getPort() : 0;
            String path = parsed.getRawPath() != null ? parsed.getRawPath() : "";
            String query = parsed.getRawQuery() != null ? parsed.getRawQuery() : "";
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
                    filterScheme(scheme), username, password, host, port, path, query, fragment);
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
        scheme = scheme.toLowerCase(Locale.ROOT);
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
            throw new HttpUriInvalidPortException(
                    "Invalid port `%" + port + "` specified; must be a valid TCP/UDP port");
        }
    }

    /**
     * The user info allows the unreserved characters, the sub-delimiters, and a colon. The colon
     * separates the username from the password, and a password can contain one.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.2.1">RFC 3986 section 3.2.1</a>
     */
    public static String filterUserInfo(String userInfo) {
        return encode(USER_INFO_PATTERN, userInfo);
    }

    /**
     * A host is either an IP literal or a reg-name. An IP literal is in brackets and holds
     * characters that a reg-name does not allow, so this method does not encode one.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">RFC 3986 section 3.2.2</a>
     */
    public static String filterHost(String host) {
        host = host.toLowerCase(Locale.ROOT);

        if (host.startsWith("[") && host.endsWith("]")) {
            return host;
        }

        return encode(HOST_PATTERN, host);
    }

    /**
     * The path allows the unreserved characters, the sub-delimiters, a colon, an at sign, and a
     * forward slash.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.3">RFC 3986 section 3.3</a>
     */
    public static String filterPath(String path) {
        validatePath(path);

        path = encode(PATH_PATTERN, path);

        if (path.startsWith("/")) {
            return "/" + path.replaceAll("^/+", "");
        }

        return path;
    }

    public static void validatePath(String path) {
        if (path.contains("?")) {
            throw new HttpUriInvalidPathException(
                    "Invalid path of `" + path + "` provided; must not contain a query string");
        }

        if (path.contains("#")) {
            throw new HttpUriInvalidPathException(
                    "Invalid path of `" + path + "` provided; must not contain a URI fragment");
        }
    }

    /**
     * The query allows the unreserved characters, the sub-delimiters, a colon, an at sign, a
     * forward slash, and a question mark.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.4">RFC 3986 section 3.4</a>
     */
    public static String filterQuery(String query) {
        validateQuery(query);

        return encode(QUERY_PATTERN, query.replaceAll("^\\?+", ""));
    }

    public static void validateQuery(String query) {
        if (query.contains("#")) {
            throw new HttpUriInvalidQueryException(
                    "Invalid query string of `"
                            + query
                            + "` provided; must not contain a URI fragment");
        }
    }

    /**
     * The fragment allows the same characters as the query.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.5">RFC 3986 section 3.5</a>
     */
    public static String filterFragment(String fragment) {
        validateFragment(fragment);

        return encode(QUERY_PATTERN, fragment.replaceAll("^#+", ""));
    }

    public static void validateFragment(String fragment) {}

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

    /**
     * Build the pattern that finds what a uri component must encode. The first group matches a
     * valid percent-encoded triplet, so the pattern claims one before it reads the percent sign as
     * a character to encode.
     *
     * @param extraAllowed the character class atoms the component allows beyond the common set
     */
    private static Pattern encodePattern(String extraAllowed) {
        return Pattern.compile(
                "(%[A-Fa-f0-9]{2})|[^" + CHAR_UNRESERVED + CHAR_SUB_DELIMS + extraAllowed + "]+");
    }

    /**
     * Percent-encode the characters that a uri component does not allow unencoded.
     *
     * <p>A character that is already part of a valid percent-encoded triplet is not encoded a
     * second time; the triplet keeps its meaning and its hexadecimal digits become uppercase. A
     * percent sign that does not begin a valid triplet is a literal percent sign, so this method
     * encodes it.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.1">RFC 3986 section 2.1</a>
     */
    private static String encode(Pattern pattern, String value) {
        Matcher matcher = pattern.matcher(value);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String triplet = matcher.group(1);
            String replacement =
                    triplet != null
                            ? triplet.toUpperCase(Locale.ROOT)
                            : percentEncode(matcher.group());

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);

        return result.toString();
    }

    /** Percent-encode every UTF-8 byte of a value that a uri component does not allow. */
    private static String percentEncode(String value) {
        StringBuilder encoded = new StringBuilder();

        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            encoded.append('%').append(String.format("%02X", b));
        }

        return encoded.toString();
    }
}
