/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.stream.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.stream.enum_.Mode;
import org.junit.jupiter.api.Test;

/** Test the {@link Mode} enum. */
final class ModeTest {

    @Test
    void getValue() {
        assertEquals("r", Mode.READ.getValue());
        assertEquals("w", Mode.WRITE.getValue());
    }

    @Test
    void readableModes() {
        assertTrue(Mode.READ.isReadable());
        assertTrue(Mode.WRITE_READ_CREATE.isReadable());
        assertFalse(Mode.WRITE.isReadable());
        assertFalse(Mode.WRITE_END.isReadable());
    }

    @Test
    void writeableModes() {
        assertTrue(Mode.WRITE.isWriteable());
        assertTrue(Mode.READ_WRITE.isWriteable());
        assertFalse(Mode.READ.isWriteable());
        assertFalse(Mode.CLOSE_ON_EXEC.isWriteable());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (Mode mode : Mode.values()) {
            assertSame(mode, Mode.valueOf(mode.name()));
            // Exercise readability/writeability for every constant.
            boolean ignored = mode.isReadable() || mode.isWriteable();
        }
    }

    @Test
    void readableAndWriteableForEveryMode() {
        for (Mode mode : Mode.values()) {
            mode.isReadable();
            mode.isWriteable();
        }
        assertTrue(Mode.READ.isReadable());
        assertFalse(Mode.WRITE.isReadable());
    }
}
