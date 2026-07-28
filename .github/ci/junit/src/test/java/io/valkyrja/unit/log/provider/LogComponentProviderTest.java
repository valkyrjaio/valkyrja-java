/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.log.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.log.provider.LogComponentProvider;
import io.valkyrja.log.provider.LogServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link LogComponentProvider}. */
final class LogComponentProviderTest {

    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void providesTheLogServiceProvider() {
        var providers = new LogComponentProvider().getContainerProviders(app);

        assertEquals(1, providers.size());
        assertInstanceOf(LogServiceProvider.class, providers.get(0));
    }

    @Test
    void providesNothingElse() {
        var provider = new LogComponentProvider();

        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
        assertTrue(provider.getGrpcProviders(app).isEmpty());
    }
}
