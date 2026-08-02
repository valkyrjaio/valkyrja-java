/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
