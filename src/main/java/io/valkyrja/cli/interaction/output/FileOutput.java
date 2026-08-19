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
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionUnwritableFileException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
        try {
            Files.writeString(
                    Path.of(filepath),
                    message.getFormattedText(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new CliInteractionUnwritableFileException(
                    "Unable to write to file " + filepath, exception);
        }
    }

    @Override
    protected Output newInstance() {
        return new FileOutput(filepath);
    }
}
