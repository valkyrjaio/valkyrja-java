/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.http.message.provider.HttpMessageComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMessageComponentProvider}. */
final class HttpMessageComponentProviderTest {

    private final HttpMessageComponentProvider provider = new HttpMessageComponentProvider();
    private final ApplicationContract app = mock(ApplicationContract.class);

    @Test
    void allProviderListsAreResolvable() {
        assertNotNull(provider.getComponentProviders(app));
        assertNotNull(provider.getContainerProviders(app));
        assertTrue(provider.getEventProviders(app).isEmpty());
        assertTrue(provider.getCliProviders(app).isEmpty());
        assertTrue(provider.getHttpProviders(app).isEmpty());
    }
}
