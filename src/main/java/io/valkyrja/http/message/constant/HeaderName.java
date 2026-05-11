/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.constant;

/**
 * Header Field Definitions.
 *
 * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC 2616 Section 14</a>
 */
public final class HeaderName {

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">RFC 2616 14.1</a> */
    public static final String ACCEPT = "Accept";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.2">RFC 2616 14.2</a> */
    public static final String ACCEPT_CHARSET = "Accept-Charset";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3">RFC 2616 14.3</a> */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">RFC 2616 14.4</a> */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.5">RFC 2616 14.5</a> */
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.6">RFC 2616 14.6</a> */
    public static final String AGE = "Age";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7">RFC 2616 14.7</a> */
    public static final String ALLOW = "Allow";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">RFC 2616 14.8</a> */
    public static final String AUTHORIZATION = "Authorization";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">RFC 2616 14.9</a> */
    public static final String CACHE_CONTROL = "Cache-Control";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.10">RFC 2616 14.10</a> */
    public static final String CONNECTION = "Connection";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">RFC 2616 14.11</a> */
    public static final String CONTENT_ENCODING = "Content-Encoding";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.12">RFC 2616 14.12</a> */
    public static final String CONTENT_LANGUAGE = "Content-Language";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">RFC 2616 14.13</a> */
    public static final String CONTENT_LENGTH = "Content-Length";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14">RFC 2616 14.14</a> */
    public static final String CONTENT_LOCATION = "Content-Location";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.15">RFC 2616 14.15</a> */
    public static final String CONTENT_MD5 = "Content-MD5";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.16">RFC 2616 14.16</a> */
    public static final String CONTENT_RANGE = "Content-Range";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">RFC 2616 14.17</a> */
    public static final String CONTENT_TYPE = "Content-Type";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18">RFC 2616 14.18</a> */
    public static final String DATE = "Date";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">RFC 2616 14.19</a> */
    public static final String E_TAG = "ETag";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.20">RFC 2616 14.20</a> */
    public static final String EXPECT = "Expect";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">RFC 2616 14.21</a> */
    public static final String EXPIRES = "Expires";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.22">RFC 2616 14.22</a> */
    public static final String FROM = "From";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23">RFC 2616 14.23</a> */
    public static final String HOST = "Host";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">RFC 2616 14.24</a> */
    public static final String IF_MATCH = "If-Match";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">RFC 2616 14.25</a> */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">RFC 2616 14.26</a> */
    public static final String IF_NONE_MATCH = "If-None-Match";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.27">RFC 2616 14.27</a> */
    public static final String IF_RANGE = "If-Range";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">RFC 2616 14.28</a> */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29">RFC 2616 14.29</a> */
    public static final String LAST_MODIFIED = "Last-Modified";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">RFC 2616 14.30</a> */
    public static final String LOCATION = "Location";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.31">RFC 2616 14.31</a> */
    public static final String MAX_FORWARDS = "Max-Forwards";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.32">RFC 2616 14.32</a> */
    public static final String PRAGMA = "Pragma";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.33">RFC 2616 14.33</a> */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.34">RFC 2616 14.34</a> */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35">RFC 2616 14.35</a> */
    public static final String RANGE = "Range";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.36">RFC 2616 14.36</a> */
    public static final String REFERER = "Referer";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.37">RFC 2616 14.37</a> */
    public static final String RETRY_AFTER = "Retry-After";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.38">RFC 2616 14.38</a> */
    public static final String SERVER = "Server";

    /** @see <a href="https://tools.ietf.org/html/rfc6265#section-4.1">RFC 6265 4.1</a> */
    public static final String SET_COOKIE = "Set-Cookie";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.39">RFC 2616 14.39</a> */
    public static final String TE = "TE";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.40">RFC 2616 14.40</a> */
    public static final String TRAILER = "Trailer";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.41">RFC 2616 14.41</a> */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.42">RFC 2616 14.42</a> */
    public static final String UPGRADE = "Upgrade";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43">RFC 2616 14.43</a> */
    public static final String USER_AGENT = "User-Agent";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44">RFC 2616 14.44</a> */
    public static final String VARY = "Vary";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.45">RFC 2616 14.45</a> */
    public static final String VIA = "Via";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46">RFC 2616 14.46</a> */
    public static final String WARNING = "Warning";

    /** @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47">RFC 2616 14.47</a> */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final String X_REQUESTED_WITH = "X-Requested-With";

    private HeaderName() {}
}
