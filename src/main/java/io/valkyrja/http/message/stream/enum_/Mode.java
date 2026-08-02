/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.enum_;

public enum Mode {
    READ("r"),
    READ_WRITE("r+"),
    WRITE("w"),
    WRITE_READ("w+"),
    WRITE_END("a"),
    WRITE_READ_END("a+"),
    CREATE_WRITE("x"),
    CREATE_WRITE_READ("x+"),
    WRITE_CREATE("c"),
    WRITE_READ_CREATE("c+"),
    CLOSE_ON_EXEC("e");

    private final String value;

    Mode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isReadable() {
        return this == READ
                || this == READ_WRITE
                || this == WRITE_READ
                || this == WRITE_READ_END
                || this == CREATE_WRITE_READ
                || this == WRITE_READ_CREATE;
    }

    public boolean isWriteable() {
        return this == READ_WRITE
                || this == WRITE
                || this == WRITE_READ
                || this == WRITE_END
                || this == WRITE_READ_END
                || this == CREATE_WRITE
                || this == CREATE_WRITE_READ
                || this == WRITE_CREATE
                || this == WRITE_READ_CREATE;
    }
}
