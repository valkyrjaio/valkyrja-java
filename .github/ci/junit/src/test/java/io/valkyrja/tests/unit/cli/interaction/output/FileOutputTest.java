/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.SuccessMessage;
import io.valkyrja.cli.interaction.output.FileOutput;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionFileWriteException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the {@link FileOutput}. */
final class FileOutputTest {

    private static final String ESCAPE = "\033";

    @TempDir Path directory;

    @Test
    void exposesAndReplacesFilepath() {
        var output = new FileOutput("/tmp/out.txt");

        assertEquals("/tmp/out.txt", output.getFilepath());
        assertEquals(
                "/tmp/other.txt",
                ((FileOutput) output.withFilepath("/tmp/other.txt")).getFilepath());
    }

    @Test
    void writesTheFormattedTextToTheFile() throws IOException {
        var filepath = directory.resolve("out.txt");
        var output = new FileOutput(filepath.toString());

        assertTrue(
                ((FileOutput) output.writeMessage(new SuccessMessage("hello")))
                        .hasWrittenMessage());
        assertEquals(
                "%s[97;42mhello%s[39;49m".formatted(ESCAPE, ESCAPE), Files.readString(filepath));
    }

    @Test
    void appendsEachMessageToTheFile() throws IOException {
        var filepath = directory.resolve("out.txt");
        var output = new FileOutput(filepath.toString());

        output.writeMessage(new Message("first"));
        output.writeMessage(new Message("second"));

        assertEquals("firstsecond", Files.readString(filepath));
    }

    @Test
    void throwsWhenTheFileIsUnwritable() {
        var filepath = directory.resolve("missing").resolve("out.txt");
        var output = new FileOutput(filepath.toString());

        assertThrows(
                CliInteractionFileWriteException.class,
                () -> output.writeMessage(new Message("hello")));
    }
}
