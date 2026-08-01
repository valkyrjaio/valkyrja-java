/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.uri.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.uri.constant.Char;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Test the {@link Char}. */
final class CharTest {

    @Test
    void unreserved() {
        assertEquals("a-zA-Z0-9_\\-\\.~", Char.UNRESERVED);
    }

    @Test
    void subDelims() {
        assertEquals("!\\$&'\\(\\)\\*\\+,;=", Char.SUB_DELIMS);
    }

    @Test
    void userInfoAddsTheColon() {
        assertEquals(Char.UNRESERVED + Char.SUB_DELIMS + ":", Char.USER_INFO);
    }

    @Test
    void hostAddsNothing() {
        assertEquals(Char.UNRESERVED + Char.SUB_DELIMS, Char.HOST);
    }

    @Test
    void pathAddsTheColonAtSignAndSlash() {
        assertEquals(Char.UNRESERVED + Char.SUB_DELIMS + ":@/", Char.PATH);
    }

    @Test
    void queryAddsTheQuestionMarkToThePathSet() {
        assertEquals(Char.PATH + "?", Char.QUERY);
    }

    /** Every component set is a valid character class, so a pattern built from one compiles. */
    @Test
    void everySetIsAValidCharacterClass() {
        for (String set : new String[] {Char.USER_INFO, Char.HOST, Char.PATH, Char.QUERY}) {
            assertTrue(Pattern.compile("[^" + set + "]").matcher(" ").find());
        }
    }
}
