/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

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
