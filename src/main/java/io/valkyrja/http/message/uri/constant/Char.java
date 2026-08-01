/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.constant;

/**
 * The characters each uri component allows unencoded, as regular expression character class atoms.
 *
 * @see <a href="https://tools.ietf.org/html/rfc3986#section-2">RFC 3986 section 2</a>
 */
public final class Char {

    /**
     * The unreserved characters, which every uri component allows.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.3">RFC 3986 section 2.3</a>
     */
    public static final String UNRESERVED = "a-zA-Z0-9_\\-\\.~";

    /**
     * The sub-delimiters, which every uri component below allows.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986 section 2.2</a>
     */
    public static final String SUB_DELIMS = "!\\$&'\\(\\)\\*\\+,;=";

    /**
     * The user info also allows the colon that separates the username from the password.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.2.1">RFC 3986 section 3.2.1</a>
     */
    public static final String USER_INFO = UNRESERVED + SUB_DELIMS + ":";

    /**
     * A reg-name allows no character beyond the common set.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.2.2">RFC 3986 section 3.2.2</a>
     */
    public static final String HOST = UNRESERVED + SUB_DELIMS;

    /**
     * The path also allows a colon, an at sign, and the segment separator.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.3">RFC 3986 section 3.3</a>
     */
    public static final String PATH = UNRESERVED + SUB_DELIMS + ":@/";

    /**
     * The query and the fragment also allow a colon, an at sign, a slash, and a question mark.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.4">RFC 3986 section 3.4</a>
     * @see <a href="https://tools.ietf.org/html/rfc3986#section-3.5">RFC 3986 section 3.5</a>
     */
    public static final String QUERY = PATH + "?";

    private Char() {}
}
