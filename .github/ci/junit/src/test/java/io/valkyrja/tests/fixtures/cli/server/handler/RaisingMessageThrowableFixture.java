/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.server.handler;

/** Testable throwable whose message raises, so a report that names it reads the stand-in. */
public final class RaisingMessageThrowableFixture extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        throw new IllegalStateException("message");
    }
}
