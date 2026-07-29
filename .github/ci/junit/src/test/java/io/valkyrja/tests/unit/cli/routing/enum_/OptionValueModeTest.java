/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionValueMode}. */
final class OptionValueModeTest {

    @Test
    void exposesAllConstants() {
        assertEquals(3, OptionValueMode.values().length);
        assertSame(OptionValueMode.NONE, OptionValueMode.valueOf("NONE"));
    }
}
