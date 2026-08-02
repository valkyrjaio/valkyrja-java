/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.enum_;

public enum ModeTranslation {
    NONE(""),
    WINDOWS("t"),
    BINARY_SAFE("b");

    private final String value;

    ModeTranslation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
