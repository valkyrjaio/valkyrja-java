/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.controller;

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
