/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output.factory.contract;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.EmptyOutputContract;
import io.valkyrja.cli.interaction.output.contract.FileOutputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.contract.PlainOutputContract;
import io.valkyrja.cli.interaction.output.contract.StreamOutputContract;
import java.io.OutputStream;

public interface OutputFactoryContract {

    OutputContract createOutput(ExitCode exitCode, MessageContract... messages);

    default OutputContract createOutput(MessageContract... messages) {
        return createOutput(ExitCode.SUCCESS, messages);
    }

    EmptyOutputContract createEmptyOutput(ExitCode exitCode, MessageContract... messages);

    default EmptyOutputContract createEmptyOutput(MessageContract... messages) {
        return createEmptyOutput(ExitCode.SUCCESS, messages);
    }

    PlainOutputContract createPlainOutput(ExitCode exitCode, MessageContract... messages);

    default PlainOutputContract createPlainOutput(MessageContract... messages) {
        return createPlainOutput(ExitCode.SUCCESS, messages);
    }

    FileOutputContract createFileOutput(
            String filepath, ExitCode exitCode, MessageContract... messages);

    default FileOutputContract createFileOutput(String filepath, MessageContract... messages) {
        return createFileOutput(filepath, ExitCode.SUCCESS, messages);
    }

    StreamOutputContract createStreamOutput(
            OutputStream stream, ExitCode exitCode, MessageContract... messages);

    default StreamOutputContract createStreamOutput(
            OutputStream stream, MessageContract... messages) {
        return createStreamOutput(stream, ExitCode.SUCCESS, messages);
    }
}
