/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import io.valkyrja.cli.interaction.output.PlainOutput;
import io.valkyrja.cli.interaction.message.Message;
import org.junit.jupiter.api.Test;

/** Test the {@link PlainOutput}. */
final class PlainOutputTest {

    @Test
    void writesUnformattedText() {
        var original = System.out;
        var buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            new PlainOutput().writeMessage(new Message("plain"));
        } finally {
            System.setOut(original);
        }

        assertEquals("plain", buffer.toString(StandardCharsets.UTF_8));
    }
}
