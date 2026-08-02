/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import org.jspecify.annotations.Nullable;

public class NewLine extends Message {

    public NewLine() {
        this(null);
    }

    public NewLine(@Nullable FormatterContract formatter) {
        super("\n", formatter);
    }
}
