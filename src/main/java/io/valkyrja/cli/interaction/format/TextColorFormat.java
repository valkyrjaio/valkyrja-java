/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.format;

import io.valkyrja.cli.interaction.enum_.TextColor;

public class TextColorFormat extends Format {

    public TextColorFormat(TextColor textColor) {
        super(String.valueOf(textColor.value), String.valueOf(textColor.getDefault()));
    }
}
