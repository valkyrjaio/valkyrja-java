/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.controller;

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
