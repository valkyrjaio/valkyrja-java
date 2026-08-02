/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.argument.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.interaction.argument.factory.ArgumentFactory;
import org.junit.jupiter.api.Test;

/** Test the {@link ArgumentFactory}. */
final class ArgumentFactoryTest {

    @Test
    void fromArgCreatesArgument() {
        assertEquals("value", ArgumentFactory.fromArg("value").getValue());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new ArgumentFactory() {});
    }
}
