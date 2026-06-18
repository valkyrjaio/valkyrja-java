/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.output.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.FileOutput;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.PlainOutput;
import io.valkyrja.cli.interaction.output.StreamOutput;
import io.valkyrja.cli.interaction.output.factory.OutputFactory;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

/** Test the {@link OutputFactory}. */
final class OutputFactoryTest {

    private final OutputFactory factory = new OutputFactory();

    @Test
    void createsEachOutputType() {
        assertInstanceOf(Output.class, factory.createOutput(ExitCode.SUCCESS));
        assertInstanceOf(EmptyOutput.class, factory.createEmptyOutput(ExitCode.SUCCESS));
        assertInstanceOf(PlainOutput.class, factory.createPlainOutput(ExitCode.SUCCESS));
        assertInstanceOf(FileOutput.class, factory.createFileOutput("/tmp/x", ExitCode.SUCCESS));
        assertInstanceOf(
                StreamOutput.class,
                factory.createStreamOutput(new ByteArrayOutputStream(), ExitCode.SUCCESS));
    }

    @Test
    void defaultOverloadsUseSuccessExitCode() {
        assertInstanceOf(Output.class, factory.createOutput());
        assertInstanceOf(EmptyOutput.class, factory.createEmptyOutput());
        assertInstanceOf(PlainOutput.class, factory.createPlainOutput());
        assertInstanceOf(FileOutput.class, factory.createFileOutput("/tmp/x"));
        assertInstanceOf(
                StreamOutput.class, factory.createStreamOutput(new ByteArrayOutputStream()));
        assertEquals(ExitCode.SUCCESS, factory.createOutput().getExitCode());
    }

    @Test
    void propagatesConfigFlagsToCreatedOutput() {
        var configured = new OutputFactory(new CliInteractionConfig(true, false, true));

        var output = configured.createOutput(ExitCode.ERROR);

        assertEquals(ExitCode.ERROR, output.getExitCode());
        assertEquals(true, output.isSilent());
    }
}