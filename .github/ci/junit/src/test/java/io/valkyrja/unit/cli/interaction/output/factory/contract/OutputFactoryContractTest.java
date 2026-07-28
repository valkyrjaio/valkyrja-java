/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.output.factory.contract;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.EmptyOutputContract;
import io.valkyrja.cli.interaction.output.contract.FileOutputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.contract.PlainOutputContract;
import io.valkyrja.cli.interaction.output.contract.StreamOutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

/** Test the {@link OutputFactoryContract}. */
final class OutputFactoryContractTest {

    /** Minimal implementation so the contract's default overloads can be exercised. */
    private static final class StubFactory implements OutputFactoryContract {
        @Override
        public OutputContract createOutput(ExitCode exitCode, MessageContract... messages) {
            return mock(OutputContract.class);
        }

        @Override
        public EmptyOutputContract createEmptyOutput(
                ExitCode exitCode, MessageContract... messages) {
            return mock(EmptyOutputContract.class);
        }

        @Override
        public PlainOutputContract createPlainOutput(
                ExitCode exitCode, MessageContract... messages) {
            return mock(PlainOutputContract.class);
        }

        @Override
        public FileOutputContract createFileOutput(
                String filepath, ExitCode exitCode, MessageContract... messages) {
            return mock(FileOutputContract.class);
        }

        @Override
        public StreamOutputContract createStreamOutput(
                java.io.OutputStream stream, ExitCode exitCode, MessageContract... messages) {
            return mock(StreamOutputContract.class);
        }
    }

    @Test
    void defaultOverloadsDelegateWithSuccessExitCode() {
        var factory = new StubFactory();

        assertNotNull(factory.createOutput());
        assertNotNull(factory.createEmptyOutput());
        assertNotNull(factory.createPlainOutput());
        assertNotNull(factory.createFileOutput("/tmp/x"));
        assertNotNull(factory.createStreamOutput(new ByteArrayOutputStream()));
    }
}
