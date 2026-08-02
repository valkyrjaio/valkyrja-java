/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.WarningFormatter;

public class WarningMessage extends Message {

    public WarningMessage(String text) {
        super(text, new WarningFormatter());
    }
}
