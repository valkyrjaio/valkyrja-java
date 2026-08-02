/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.format;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;

public class BackgroundColorFormat extends Format {

    public BackgroundColorFormat(BackgroundColor backgroundColor) {
        super(String.valueOf(backgroundColor.value), String.valueOf(backgroundColor.getDefault()));
    }
}
