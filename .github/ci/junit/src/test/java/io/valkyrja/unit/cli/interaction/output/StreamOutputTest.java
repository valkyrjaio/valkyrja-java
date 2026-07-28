/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.output.StreamOutput;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

/** Test the {@link StreamOutput}. */
final class StreamOutputTest {

    @Test
    void exposesAndReplacesStream() {
        var stream = new ByteArrayOutputStream();
        var output = new StreamOutput(stream);

        assertSame(stream, output.getStream());

        var other = new ByteArrayOutputStream();
        assertSame(other, ((StreamOutput) output.withStream(other)).getStream());
        assertTrue(((StreamOutput) output.writeMessage(new Message("x"))).hasWrittenMessage());
    }
}
