/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.reflection.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.reflection.provider.ReflectionComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link ReflectionComponentProvider}. */
final class ReflectionComponentProviderTest {

    private final ReflectionComponentProvider provider = new ReflectionComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void allProviderListsAreEmpty() {
        assertTrue(provider.getComponentProviders(app).isEmpty());
        assertTrue(provider.getContainerProviders(app).isEmpty());
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}