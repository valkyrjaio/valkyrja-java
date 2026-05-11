/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.factory;

import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidNameException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidValueException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class HeaderFactory {

    private static final Pattern CRLF_ATTACK_PATTERN = Pattern.compile(
        "(?:(?:(?<!\\r)\\n)|(?:\\r(?!\\n))|(?:\\r\\n(?![ \\t])))"
    );

    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile(
        "[^\\x09\\x0a\\x0d\\x20-\\x7E\\x80-\\xFE]"
    );

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9'`#$%&*+.^_|~!\\-]+$"
    );

    public static Map<String, HeaderContract> marshalHeaders(Map<String, String> server) {
        Map<String, HeaderContract> headers = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : server.entrySet()) {
            marshalHeader(server, headers, entry.getKey(), entry.getValue());
        }

        return headers;
    }

    public static String filterValue(String value) {
        int length = value.length();
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int ascii = value.charAt(i);

            if (ascii == 13) {
                if (i + 2 < length) {
                    int lf = value.charAt(i + 1);
                    int ws = value.charAt(i + 2);

                    if (lf == 10 && (ws == 9 || ws == 32)) {
                        string.append(value.charAt(i));
                        string.append(value.charAt(i + 1));
                        i++;
                    }
                }

                continue;
            }

            if (isInvalidValueAscii(ascii)) {
                continue;
            }

            string.append(value.charAt(i));
        }

        return string.toString();
    }

    public static void assertValidValue(String value) {
        if (!isValidValue(value)) {
            throw new HttpHeaderInvalidValueException(String.format("\"%s\" is not valid header value", value));
        }
    }

    public static boolean isValidValue(String value) {
        if (CRLF_ATTACK_PATTERN.matcher(value).find()) {
            return false;
        }

        if (INVALID_CHARS_PATTERN.matcher(value).find()) {
            return false;
        }

        return true;
    }

    public static void assertValidName(String name) {
        if (!isValidName(name)) {
            throw new HttpHeaderInvalidNameException(String.format("\"%s\" is not valid header name", name));
        }
    }

    public static boolean isValidName(String name) {
        return !name.isEmpty() && VALID_NAME_PATTERN.matcher(name).matches();
    }

    protected static boolean isInvalidValueAscii(int ascii) {
        return (ascii < 32 && ascii != 9)
            || ascii == 127
            || ascii > 254;
    }

    protected static void marshalHeader(
        Map<String, String> server,
        Map<String, HeaderContract> headers,
        String key,
        String value
    ) {
        if (key.startsWith("REDIRECT_")) {
            key = key.substring(9);

            if (server.containsKey(key)) {
                return;
            }
        }

        if (isValidHttpHeader(key, value)) {
            String name = key.substring(5).replace('_', '-').toLowerCase();
            headers.put(name, new Header(name, value));
            return;
        }

        if (isValidHttpContentHeader(key, value)) {
            String name = "content-" + key.substring(8).toLowerCase();
            headers.put(name, new Header(name, value));
        }
    }

    protected static boolean isValidHttpHeader(String name, String value) {
        return value != null && name.startsWith("HTTP_");
    }

    protected static boolean isValidHttpContentHeader(String name, String value) {
        return value != null && name.startsWith("CONTENT_");
    }
}
