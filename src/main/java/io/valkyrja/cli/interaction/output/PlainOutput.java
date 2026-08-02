/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.output;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.PlainOutputContract;

public class PlainOutput extends Output implements PlainOutputContract {

    public PlainOutput() {
        super();
    }

    public PlainOutput(
            boolean isInteractive,
            boolean isQuiet,
            boolean isSilent,
            ExitCode exitCode,
            MessageContract... messages) {
        super(isInteractive, isQuiet, isSilent, exitCode, messages);
    }

    @Override
    protected void outputMessage(MessageContract message) {
        System.out.print(message.getText());
    }

    @Override
    protected Output newInstance() {
        return new PlainOutput();
    }
}
