/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.enum_;

import java.util.List;

/**
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-method-definitions">RFC 9110 Method Definitions</a>
 */
public enum RequestMethod {

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-get">RFC 9110 GET</a> */
    GET("GET"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-head">RFC 9110 HEAD</a> */
    HEAD("HEAD"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-post">RFC 9110 POST</a> */
    POST("POST"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-put">RFC 9110 PUT</a> */
    PUT("PUT"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-delete">RFC 9110 DELETE</a> */
    DELETE("DELETE"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-connect">RFC 9110 CONNECT</a> */
    CONNECT("CONNECT"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-options">RFC 9110 OPTIONS</a> */
    OPTIONS("OPTIONS"),

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc9110.html#name-trace">RFC 9110 TRACE</a> */
    TRACE("TRACE"),

    /**
     * @see <a href="https://tools.ietf.org/html/rfc5789">RFC 5789</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/PATCH">MDN PATCH</a>
     */
    PATCH("PATCH"),

    ANY("ANY");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<RequestMethod> all() {
        return List.of(GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH);
    }

    public static RequestMethod from(String value) {
        for (RequestMethod method : values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown request method: " + value);
    }
}