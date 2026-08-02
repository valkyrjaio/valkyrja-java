/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.data.CliConfig;
import io.valkyrja.application.entry.Cli;
import io.valkyrja.cli.server.support.Exiter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link Cli} entry point. */
final class CliTest {

    // Freeze the exiter so dispatching a command does not call System.exit and kill the test JVM.
    @BeforeEach
    void freezeExiter() {
        Exiter.freeze();
    }

    @AfterEach
    void unfreezeExiter() {
        Exiter.unfreeze();
    }

    @Test
    void runBootstrapsAndDispatchesTheDefaultCommand() {
        assertDoesNotThrow(() -> Cli.run(new CliConfig(), new String[] {}));
    }

    @Test
    void runDispatchesAnExplicitCommandFromArgs() {
        assertDoesNotThrow(() -> Cli.run(new CliConfig(), new String[] {"valkyrja", "list"}));
    }

    @Test
    void isInstantiable() {
        assertNotNull(new Cli());
    }
}
