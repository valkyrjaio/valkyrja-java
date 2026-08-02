/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.routing.controller.Controller;
import org.junit.jupiter.api.Test;

/** Test the abstract http routing {@link Controller} base. */
final class ControllerTest {

    @Test
    void constructorStoresDependencies() {
        var controller =
                new Controller(
                        mock(ServerRequestContract.class), mock(ResponseFactoryContract.class)) {};

        assertNotNull(controller);
    }
}
