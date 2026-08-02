/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.controller.Controller;
import org.junit.jupiter.api.Test;

/** Test the abstract {@link Controller} base. */
final class ControllerTest {

    @Test
    void constructorStoresDependencies() {
        var controller =
                new Controller(mock(InputContract.class), mock(OutputFactoryContract.class)) {};

        assertNotNull(controller);
    }
}
