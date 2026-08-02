/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.SuccessFormatter;

public class SuccessMessage extends Message {

    public SuccessMessage(String text) {
        super(text, new SuccessFormatter());
    }
}
