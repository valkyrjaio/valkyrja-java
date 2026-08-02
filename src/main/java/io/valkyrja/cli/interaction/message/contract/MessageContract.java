/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message.contract;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;

public interface MessageContract {

    String getText();

    String getFormattedText();

    MessageContract withText(String text);

    boolean hasFormatter();

    FormatterContract getFormatter();

    MessageContract withFormatter(FormatterContract formatter);

    MessageContract withoutFormatter();
}
