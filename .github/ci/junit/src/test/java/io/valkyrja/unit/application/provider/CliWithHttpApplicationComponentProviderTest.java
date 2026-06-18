/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.provider.CliApplicationComponentProvider;
import io.valkyrja.application.provider.CliWithHttpApplicationComponentProvider;
import org.junit.jupiter.api.Test;

final class CliWithHttpApplicationComponentProviderTest {

    private final CliWithHttpApplicationComponentProvider provider =
            new CliWithHttpApplicationComponentProvider();

    @Test
    void componentProvidersIncludeCliAndHttpStack() {
        var components = provider.getComponentProviders(null);

        assertEquals(6, components.size());
        assertInstanceOf(CliApplicationComponentProvider.class, components.get(0));
    }

    @Test
    void otherProviderListsAreEmpty() {
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
    }
}