/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.factory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class CookieFactory {

    private static final Pattern COOKIE_PATTERN =
            Pattern.compile(
                    "(?:(?:\\n?[ \\t]*)|(?:; ))(?<name>[!#$%&'*+\\-.0-9A-Z^_`a-z|~]+)=(?<DQUOTE>\"?)(?<value>[\\x21\\x23-\\x2b\\x2d-\\x3a\\x3c-\\x5b\\x5d-\\x7e]*)\\k<DQUOTE>(?=\\n?[ \\t]*$|; )");

    public static Map<String, String> parseCookieHeader(String cookieHeader) {
        Map<String, String> cookies = new LinkedHashMap<>();
        Matcher matcher = COOKIE_PATTERN.matcher(cookieHeader);

        while (matcher.find()) {
            String name = matcher.group("name");
            String value = matcher.group("value");
            cookies.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }

        return cookies;
    }

    public static String convertCookieArrayToHeaderString(Map<String, String> cookies) {
        return cookies.entrySet().stream()
                .map(entry -> combineKeyAndValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
    }

    public static String combineKeyAndValue(String key, String value) {
        return key + "=" + value;
    }
}
