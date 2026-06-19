/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.stream.enum_.PhpWrapper;
import org.junit.jupiter.api.Test;

/** Test the {@link PhpWrapper} enum. */
final class PhpWrapperTest {

    @Test
    void getValue() {
        assertEquals("php://memory", PhpWrapper.memory.getValue());
        assertEquals("php://temp", PhpWrapper.temp.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (PhpWrapper w : PhpWrapper.values()) {
            assertSame(w, PhpWrapper.valueOf(w.name()));
        }
    }
}
