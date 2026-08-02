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
import io.valkyrja.cli.interaction.output.contract.FileOutputContract;

public class FileOutput extends Output implements FileOutputContract {

    protected String filepath;

    public FileOutput(String filepath) {
        this(filepath, true, false, false, ExitCode.SUCCESS);
    }

    public FileOutput(
            String filepath,
            boolean isInteractive,
            boolean isQuiet,
            boolean isSilent,
            ExitCode exitCode,
            MessageContract... messages) {
        super(isInteractive, isQuiet, isSilent, exitCode, messages);
        this.filepath = filepath;
    }

    @Override
    public String getFilepath() {
        return filepath;
    }

    @Override
    public FileOutputContract withFilepath(String filepath) {
        FileOutput copy = (FileOutput) copy();
        copy.filepath = filepath;
        return copy;
    }

    @Override
    protected void outputMessage(MessageContract message) {
        // TODO: Implement
    }

    @Override
    protected Output newInstance() {
        return new FileOutput(filepath);
    }
}
