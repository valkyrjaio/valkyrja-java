/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.data.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.constant.OptionName;
import io.valkyrja.cli.routing.data.option.HelpOptionParameter;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the {@link HelpOptionParameter}. */
final class HelpOptionParameterTest {

    @Test
    void isValuelessOptionWithExpectedName() {
        var option = new HelpOptionParameter();

        assertEquals(OptionName.HELP, option.getName());
        assertSame(OptionValueMode.NONE, option.getValueMode());
        assertFalse(option.getShortNames().isEmpty());
    }
}
