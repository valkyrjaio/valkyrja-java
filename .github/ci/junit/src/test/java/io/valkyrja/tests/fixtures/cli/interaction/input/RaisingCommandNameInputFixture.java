/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.interaction.input;

import io.valkyrja.cli.interaction.input.Input;

/** Testable Input whose command name raises, so a report that reads the input raises with it. */
public final class RaisingCommandNameInputFixture extends Input {

    @Override
    public String getCommandName() {
        throw new IllegalStateException("input");
    }
}
