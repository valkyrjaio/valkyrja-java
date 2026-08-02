/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.writer.contract;

import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;

public interface WriterContract {

    boolean shouldWriteMessage(MessageContract message);

    OutputContract write(OutputContract output, MessageContract message);
}
