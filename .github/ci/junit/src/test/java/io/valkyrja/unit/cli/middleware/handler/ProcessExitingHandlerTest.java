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

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.ProcessExitingHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.fixtures.cli.middleware.PassThroughMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link ProcessExitingHandler}. */
final class ProcessExitingHandlerTest {

    @Test
    void runsWithAndWithoutMiddleware() {
        var container = new Container();
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());

        assertDoesNotThrow(
                () ->
                        new ProcessExitingHandler(new Container())
                                .processExiting(
                                        mock(InputContract.class), mock(OutputContract.class)));
        assertDoesNotThrow(
                () ->
                        new ProcessExitingHandler(container, PassThroughMiddleware.class)
                                .processExiting(
                                        mock(InputContract.class), mock(OutputContract.class)));
    }
}
