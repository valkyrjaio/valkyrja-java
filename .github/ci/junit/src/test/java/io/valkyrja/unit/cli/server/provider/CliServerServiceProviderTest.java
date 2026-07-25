/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.contract.InputHandlerContract;
import io.valkyrja.container.manager.Container;

import io.valkyrja.cli.server.provider.CliServerServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link CliServerServiceProvider}. */
final class CliServerServiceProviderTest {

    @Test
    void publishersExposesInputHandler() {
        assertEquals(1, new CliServerServiceProvider().publishers().size());
    }

    @Test
    void publishInputHandlerBindsHandler() {
        var container = new Container();
        container.setSingleton(RouterContract.class, mock(RouterContract.class));
        container.setSingleton(
                InputReceivedHandlerContract.class, mock(InputReceivedHandlerContract.class));
        container.setSingleton(
                ThrowableCaughtHandlerContract.class, mock(ThrowableCaughtHandlerContract.class));
        container.setSingleton(ProcessExitingHandlerContract.class, mock(ProcessExitingHandlerContract.class));
        container.setSingleton(
                CliInteractionConfigContract.class, mock(CliInteractionConfigContract.class));

        CliServerServiceProvider.publishInputHandler(container);

        assertNotNull(container.getSingleton(InputHandlerContract.class));
    }
}
