/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.enum_;

public enum PhpWrapper {
    stdin("php://stdin"),
    stdout("php://stdout"),
    stderr("php://stderr"),
    input("php://input"),
    output("php://output"),
    fd("php://fd"),
    memory("php://memory"),
    temp("php://temp"),
    filter("php://filter");

    private final String value;

    PhpWrapper(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
