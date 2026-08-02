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
import io.valkyrja.cli.routing.data.option.QuietOptionParameter;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the {@link QuietOptionParameter}. */
final class QuietOptionParameterTest {

    @Test
    void isValuelessOptionWithExpectedName() {
        var option = new QuietOptionParameter();

        assertEquals(OptionName.QUIET, option.getName());
        assertSame(OptionValueMode.NONE, option.getValueMode());
        assertFalse(option.getShortNames().isEmpty());
    }
}
