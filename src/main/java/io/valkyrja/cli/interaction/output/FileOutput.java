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
