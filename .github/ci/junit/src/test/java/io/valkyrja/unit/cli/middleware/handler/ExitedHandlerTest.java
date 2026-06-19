/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import io.valkyrja.classes.cli.middleware.PassThroughMiddleware;
import io.valkyrja.container.manager.Container;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.middleware.handler.ExitedHandler;
import org.junit.jupiter.api.Test;

/** Test the {@link ExitedHandler}. */
final class ExitedHandlerTest {

    @Test
    void runsWithAndWithoutMiddleware() {
        var container = new Container();
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());

        assertDoesNotThrow(
                () ->
                        new ExitedHandler(new Container())
                                .exited(mock(InputContract.class), mock(OutputContract.class)));
        assertDoesNotThrow(
                () ->
                        new ExitedHandler(container, PassThroughMiddleware.class)
                                .exited(mock(InputContract.class), mock(OutputContract.class)));
    }
}
