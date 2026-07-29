/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.enum_;

import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.enum_.OptionType;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionType} enum. */
final class OptionTypeTest {

    @Test
    void valueOfResolvesEachConstant() {
        for (OptionType type : OptionType.values()) {
            assertSame(type, OptionType.valueOf(type.name()));
        }
    }
}
