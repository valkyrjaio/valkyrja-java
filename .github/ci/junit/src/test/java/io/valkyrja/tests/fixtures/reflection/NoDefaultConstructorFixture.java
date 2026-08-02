/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.reflection;

/** Class without a no-arg constructor — reflection instantiation must fail. */
public final class NoDefaultConstructorFixture {

    private final String value;

    public NoDefaultConstructorFixture(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
