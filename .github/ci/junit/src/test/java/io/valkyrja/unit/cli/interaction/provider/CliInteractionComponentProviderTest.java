/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.interaction.provider.CliInteractionComponentProvider;
import io.valkyrja.cli.interaction.provider.CliInteractionServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link CliInteractionComponentProvider}. */
final class CliInteractionComponentProviderTest {

    private final CliInteractionComponentProvider provider = new CliInteractionComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void providesCliInteractionServiceProvider() {
        var containerProviders = provider.getContainerProviders(app);

        assertEquals(1, containerProviders.size());
        assertInstanceOf(CliInteractionServiceProvider.class, containerProviders.get(0));
    }

    @Test
    void otherProviderListsAreEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}