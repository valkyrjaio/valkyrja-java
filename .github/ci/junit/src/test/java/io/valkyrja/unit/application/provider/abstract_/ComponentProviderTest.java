/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.provider.abstract_;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.application.provider.abstract_.ComponentProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link ComponentProvider} base. */
final class ComponentProviderTest {

    /** A component that contributes nothing, inheriting every default. */
    private static final class EmptyComponentProvider extends ComponentProvider {}

    private final EmptyComponentProvider provider = new EmptyComponentProvider();

    @Test
    void contributesNothingByDefault() {
        assertTrue(provider.getComponentProviders(null).isEmpty());
        assertTrue(provider.getContainerProviders(null).isEmpty());
        assertTrue(provider.getEventProviders(null).isEmpty());
        assertTrue(provider.getCliProviders(null).isEmpty());
        assertTrue(provider.getHttpProviders(null).isEmpty());
        assertTrue(provider.getGrpcProviders(null).isEmpty());
    }
}
