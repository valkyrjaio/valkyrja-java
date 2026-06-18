/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.container.provider;
import io.valkyrja.container.provider.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import org.junit.jupiter.api.Test;

final class ContainerComponentProviderTest {

    private final ContainerComponentProvider provider = new ContainerComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void getComponentProvidersIsEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
    }

    @Test
    void getContainerProvidersReturnsServiceProvider() {
        assertInstanceOf(ServiceProvider.class, provider.getContainerProviders(app).get(0));
    }

    @Test
    void getEventProvidersIsEmpty() {
        assertTrue(provider.getEventProviders(app).isEmpty());
    }

    @Test
    void getCliProvidersIsEmpty() {
        assertTrue(provider.getCliProviders(app).isEmpty());
    }

    @Test
    void getHttpProvidersIsEmpty() {
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }

    @Test
    void getContainerProvidersReturnsExactlyOne() {
        assertEquals(1, provider.getContainerProviders(app).size());
    }
}