/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.FileOutput;
import org.junit.jupiter.api.Test;

/** Test the {@link FileOutput}. */
final class FileOutputTest {

    @Test
    void exposesAndReplacesFilepath() {
        var output = new FileOutput("/tmp/out.txt");

        assertEquals("/tmp/out.txt", output.getFilepath());
        assertEquals(
                "/tmp/other.txt",
                ((FileOutput) output.withFilepath("/tmp/other.txt")).getFilepath());
        assertTrue(((FileOutput) output.writeMessage(new Message("x"))).hasWrittenMessage());
    }
}
