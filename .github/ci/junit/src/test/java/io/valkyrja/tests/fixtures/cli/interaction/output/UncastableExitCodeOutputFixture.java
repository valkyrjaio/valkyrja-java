/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.interaction.output;

import io.valkyrja.cli.interaction.output.Output;

/** Testable Output whose exit code is neither an ExitCode nor an int, so the cast refuses it. */
public final class UncastableExitCodeOutputFixture extends Output {

    @Override
    public Object getExitCode() {
        return "not a code";
    }

    @Override
    protected Output newInstance() {
        return new UncastableExitCodeOutputFixture();
    }
}
