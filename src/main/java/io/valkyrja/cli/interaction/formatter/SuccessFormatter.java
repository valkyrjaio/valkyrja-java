/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.formatter;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.BackgroundColorFormat;
import io.valkyrja.cli.interaction.format.TextColorFormat;

public class SuccessFormatter extends Formatter {

    public SuccessFormatter() {
        super(
                new TextColorFormat(TextColor.LIGHT_WHITE),
                new BackgroundColorFormat(BackgroundColor.GREEN));
    }
}
