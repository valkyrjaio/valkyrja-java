/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.enum_;

public enum Port {
    HTTP(80),
    HTTPS(433);

    private final int value;

    Port(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
