/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.StreamOutputContract;
import java.io.OutputStream;

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
        // TODO: Implement
    }

    @Override
    protected Output newInstance() {
        return new StreamOutput(stream);
    }
}