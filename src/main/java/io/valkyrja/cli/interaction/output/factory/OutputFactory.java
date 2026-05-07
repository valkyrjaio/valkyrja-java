/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output.factory;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.PlainOutput;
import io.valkyrja.cli.interaction.output.contract.EmptyOutputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.contract.PlainOutputContract;
import io.valkyrja.cli.interaction.output.contract.StreamOutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import java.io.OutputStream;

public class OutputFactory implements OutputFactoryContract {

    protected final CliInteractionConfigContract config;

    public OutputFactory() {
        this(new CliInteractionConfig());
    }

    public OutputFactory(CliInteractionConfigContract config) {
        this.config = config;
    }

    @Override
    public OutputContract createOutput(ExitCode exitCode, MessageContract... messages) {
        return new Output(config.isInteractive(), config.isQuiet(), config.isSilent(), exitCode, messages);
    }

    @Override
    public EmptyOutputContract createEmptyOutput(ExitCode exitCode, MessageContract... messages) {
        EmptyOutput out = new EmptyOutput();
        applyConfig(out, exitCode, messages);
        return out;
    }

    @Override
    public PlainOutputContract createPlainOutput(ExitCode exitCode, MessageContract... messages) {
        PlainOutput out = new PlainOutput();
        applyConfig(out, exitCode, messages);
        return out;
    }

    @Override
    public StreamOutputContract createStreamOutput(OutputStream stream, ExitCode exitCode, MessageContract... messages) {
        throw new UnsupportedOperationException("StreamOutput not yet implemented");
    }

    protected void applyConfig(Output out, ExitCode exitCode, MessageContract[] messages) {
        out.isInteractive = config.isInteractive();
        out.isQuiet = config.isQuiet();
        out.isSilent = config.isSilent();
        out.exitCode = exitCode;
        for (MessageContract m : messages) {
            out.unwrittenMessages.add(m);
        }
    }
}
