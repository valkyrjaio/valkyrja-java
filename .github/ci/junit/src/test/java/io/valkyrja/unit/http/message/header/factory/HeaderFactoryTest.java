/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidNameException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidValueException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link HeaderFactory}. */
final class HeaderFactoryTest {

    @Test
    void filterValueStripsControlCharacters() {
        assertEquals("ab", HeaderFactory.filterValue("a\u0001b\u007F"));
    }

    @Test
    void filterValuePreservesObsoleteLineFolding() {
        assertEquals("a\r\n\tb", HeaderFactory.filterValue("a\r\n\tb"));
    }

    @Test
    void isValidValue() {
        assertTrue(HeaderFactory.isValidValue("normal value"));
        assertFalse(HeaderFactory.isValidValue("a\r\nb"));
    }

    @Test
    void assertValidValueThrowsForInvalid() {
        assertThrows(
                HttpHeaderInvalidValueException.class,
                () -> HeaderFactory.assertValidValue("a\r\nb"));
    }

    @Test
    void isValidName() {
        assertTrue(HeaderFactory.isValidName("Content-Type"));
        assertFalse(HeaderFactory.isValidName(""));
        assertFalse(HeaderFactory.isValidName("bad name"));
    }

    @Test
    void assertValidNameThrowsForInvalid() {
        assertThrows(
                HttpHeaderInvalidNameException.class,
                () -> HeaderFactory.assertValidName("bad name"));
    }

    @Test
    void marshalHeadersConvertsHttpAndContentKeys() {
        Map<String, String> server = new LinkedHashMap<>();
        server.put("HTTP_X_CUSTOM", "value");
        server.put("CONTENT_TYPE", "text/html");
        server.put("NON_HEADER", "ignored");

        Map<String, HeaderContract> headers = HeaderFactory.marshalHeaders(server);

        assertTrue(headers.containsKey("x-custom"));
        assertTrue(headers.containsKey("content-type"));
        assertFalse(headers.containsKey("non-header"));
    }

    @Test
    void marshalHeadersHandlesRedirectKeys() {
        Map<String, String> withoutOriginal = new LinkedHashMap<>();
        withoutOriginal.put("REDIRECT_HTTP_X_FOO", "value");
        assertTrue(HeaderFactory.marshalHeaders(withoutOriginal).containsKey("x-foo"));

        Map<String, String> withOriginal = new LinkedHashMap<>();
        withOriginal.put("HTTP_X_FOO", "original");
        withOriginal.put("REDIRECT_HTTP_X_FOO", "redirected");
        // The REDIRECT_ duplicate is skipped because the original is present.
        assertEquals("original", HeaderFactory.marshalHeaders(withOriginal).get("x-foo").getHeaderLine());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new HeaderFactory() {});
    }
}
