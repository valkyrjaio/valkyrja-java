/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.reflection;

/** Simple reflectable class with a public no-arg constructor and a public method. */
public final class ReflectableFixture {

    public String greet() {
        return "hello";
    }
}
