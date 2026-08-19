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
import io.valkyrja.cli.interaction.output.contract.StreamOutputContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionStreamWriteException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StreamOutput extends Output implements StreamOutputContract {

    protected OutputStream stream;

    public StreamOutput(OutputStream stream) {
        this(stream, true, false, false, ExitCode.SUCCESS);
    }

    public StreamOutput(
            OutputStream stream,
            boolean isInteractive,
            boolean isQuiet,
            boolean isSilent,
            ExitCode exitCode,
            MessageContract... messages) {
        super(isInteractive, isQuiet, isSilent, exitCode, messages);
        this.stream = stream;
    }

    @Override
    public OutputStream getStream() {
        return stream;
    }

    @Override
    public StreamOutputContract withStream(OutputStream stream) {
        StreamOutput copy = (StreamOutput) copy();
        copy.stream = stream;
        return copy;
    }

    @Override
    protected void outputMessage(MessageContract message) {
        try {
            stream.write(message.getFormattedText().getBytes(StandardCharsets.UTF_8));
            stream.flush();
        } catch (IOException exception) {
            throw new CliInteractionStreamWriteException(
                    "Unable to write to the stream", exception);
        }
    }

    @Override
    protected Output newInstance() {
        return new StreamOutput(stream);
    }
}
