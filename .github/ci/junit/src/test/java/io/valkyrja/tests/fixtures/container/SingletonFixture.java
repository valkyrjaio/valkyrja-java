/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.container;

import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;

/** Testable singleton with a no-arg constructor. */
public final class SingletonFixture {

    public static SingletonFixture make(
            ContainerContract container, Map<String, Object> arguments) {
        return new SingletonFixture();
    }
}
