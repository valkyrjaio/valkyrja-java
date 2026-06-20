/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Exercises {@link HeaderFactory}'s protected validators and obs-fold handling directly. */
final class HeaderFactoryInternalsTest {

    @Test
    void isInvalidValueAsciiCoversEveryCondition() {
        assertTrue(HeaderFactory.isInvalidValueAscii(0)); // < 32 and != 9
        assertFalse(HeaderFactory.isInvalidValueAscii(9)); // tab is allowed
        assertTrue(HeaderFactory.isInvalidValueAscii(127)); // == 127
        assertTrue(HeaderFactory.isInvalidValueAscii(255)); // > 254
        assertFalse(HeaderFactory.isInvalidValueAscii('A')); // ordinary character
    }

    @Test
    void isValidHttpHeaderBranches() {
        assertTrue(HeaderFactory.isValidHttpHeader("HTTP_X", "v"));
        assertFalse(HeaderFactory.isValidHttpHeader("HTTP_X", null));
        assertFalse(HeaderFactory.isValidHttpHeader("OTHER", "v"));
    }

    @Test
    void isValidHttpContentHeaderBranches() {
        assertTrue(HeaderFactory.isValidHttpContentHeader("CONTENT_TYPE", "v"));
        assertFalse(HeaderFactory.isValidHttpContentHeader("CONTENT_TYPE", null));
        assertFalse(HeaderFactory.isValidHttpContentHeader("OTHER", "v"));
    }

    @Test
    void filterValueObsFoldBranches() {
        // Valid obs-fold (CR LF followed by whitespace) is preserved (tab and space).
        assertEquals("a\r\n\tb", HeaderFactory.filterValue("a\r\n\tb"));
        assertEquals("a\r\n b", HeaderFactory.filterValue("a\r\n b"));
        // CR LF not followed by whitespace is dropped.
        assertEquals("aXb", HeaderFactory.filterValue("a\r\nXb"));
        // CR not followed by LF is dropped.
        assertEquals("abc", HeaderFactory.filterValue("a\rbc"));
        // A trailing CR has no room for a following LF + whitespace.
        assertEquals("ab", HeaderFactory.filterValue("ab\r"));
    }
}
