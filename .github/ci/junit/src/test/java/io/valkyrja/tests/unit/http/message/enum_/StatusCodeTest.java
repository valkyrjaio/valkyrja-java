/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import org.junit.jupiter.api.Test;

/** Test the {@link StatusCode} enum. */
final class StatusCodeTest {

    @Test
    void valueAndCode() {
        assertEquals(200, StatusCode.OK.getValue());
        assertEquals(200, StatusCode.OK.code());
    }

    @Test
    void asPhrase() {
        assertFalse(StatusCode.OK.asPhrase().isEmpty());
    }

    @Test
    void isRedirect() {
        assertTrue(StatusCode.FOUND.isRedirect());
        assertFalse(StatusCode.OK.isRedirect());
    }

    @Test
    void isError() {
        assertTrue(StatusCode.INTERNAL_SERVER_ERROR.isError());
        assertFalse(StatusCode.OK.isError());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (StatusCode code : StatusCode.values()) {
            assertSame(code, StatusCode.valueOf(code.name()));
        }
    }

    @Test
    void isRedirectCoversBoundaries() {
        assertFalse(StatusCode.OK.isRedirect());
        assertTrue(StatusCode.FOUND.isRedirect());
        assertFalse(StatusCode.BAD_REQUEST.isRedirect());
    }
}
