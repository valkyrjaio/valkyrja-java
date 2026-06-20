/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.constant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.Port;
import org.junit.jupiter.api.Test;

/** Test the {@link Port} constant holder. */
final class PortTest {

    @Test
    void isValidChecksRange() {
        assertTrue(Port.isValid(80));
        assertFalse(Port.isValid(0));
        assertFalse(Port.isValid(70000));
    }
}
