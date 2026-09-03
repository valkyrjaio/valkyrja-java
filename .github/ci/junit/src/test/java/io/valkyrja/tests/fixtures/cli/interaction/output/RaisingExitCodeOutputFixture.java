/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.interaction.output;

import io.valkyrja.cli.interaction.output.Output;

/** Testable Output whose exit code raises, so a read of it raises with it. */
public final class RaisingExitCodeOutputFixture extends Output {

    @Override
    public Object getExitCode() {
        throw new IllegalStateException("exit code");
    }

    @Override
    protected Output newInstance() {
        return new RaisingExitCodeOutputFixture();
    }
}
