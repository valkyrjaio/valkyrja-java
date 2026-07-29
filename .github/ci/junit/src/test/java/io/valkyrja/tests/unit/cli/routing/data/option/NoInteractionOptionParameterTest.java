/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.data.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.constant.OptionName;
import io.valkyrja.cli.routing.data.option.NoInteractionOptionParameter;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the {@link NoInteractionOptionParameter}. */
final class NoInteractionOptionParameterTest {

    @Test
    void isValuelessOptionWithExpectedName() {
        var option = new NoInteractionOptionParameter();

        assertEquals(OptionName.NO_INTERACTION, option.getName());
        assertSame(OptionValueMode.NONE, option.getValueMode());
        assertFalse(option.getShortNames().isEmpty());
    }
}
