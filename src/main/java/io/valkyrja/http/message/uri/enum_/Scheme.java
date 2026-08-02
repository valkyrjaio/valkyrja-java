/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.uri.enum_;

public enum Scheme {
    EMPTY(""),
    HTTP("http"),
    HTTPS("https");

    private final String value;

    Scheme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
