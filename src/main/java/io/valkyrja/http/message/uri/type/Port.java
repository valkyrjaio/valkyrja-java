/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.type;

import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;

public class Port {

    private final int subject;

    public Port(int subject) {
        if (subject >= 1 && subject <= 65535) {
            this.subject = subject;
            return;
        }

        throw new HttpUriInvalidPortException("Invalid port argument passed.");
    }

    public static Port fromValue(Object value) {
        if (!(value instanceof Integer)) {
            throw new HttpUriInvalidPortException(
                    "Int expected value of type `"
                            + (value == null ? "null" : value.getClass().getSimpleName())
                            + "` provided");
        }

        return new Port((Integer) value);
    }

    public int asFlatValue() {
        return subject;
    }
}
