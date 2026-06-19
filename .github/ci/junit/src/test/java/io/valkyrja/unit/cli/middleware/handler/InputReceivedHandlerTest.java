/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.classes.cli.middleware.PassThroughMiddleware;
import io.valkyrja.container.manager.Container;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.middleware.handler.InputReceivedHandler;
import org.junit.jupiter.api.Test;

/** Test the {@link InputReceivedHandler}. */
final class InputReceivedHandlerTest {

    @Test
    void returnsInputWithoutMiddleware() {
        var input = mock(InputContract.class);

        assertSame(input, new InputReceivedHandler(new Container()).inputReceived(input));
    }

    @Test
    void runsMiddlewareChain() {
        var container = new Container();
        container.setSingleton(PassThroughMiddleware.class, new PassThroughMiddleware());
        var input = mock(InputContract.class);

        assertSame(
                input,
                new InputReceivedHandler(container, PassThroughMiddleware.class)
                        .inputReceived(input));
    }
}
