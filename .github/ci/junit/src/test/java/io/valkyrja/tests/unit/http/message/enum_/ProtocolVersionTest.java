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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import org.junit.jupiter.api.Test;

/** Test the {@link ProtocolVersion} enum. */
final class ProtocolVersionTest {

    @Test
    void getValue() {
        assertEquals("1.1", ProtocolVersion.V1_1.getValue());
    }

    @Test
    void fromResolvesAndRejects() {
        assertSame(ProtocolVersion.V2, ProtocolVersion.from("2"));
        assertThrows(IllegalArgumentException.class, () -> ProtocolVersion.from("9"));
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (ProtocolVersion v : ProtocolVersion.values()) {
            assertSame(v, ProtocolVersion.valueOf(v.name()));
        }
    }
}
