/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.container.manager.abstract_;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.throwable.exception.ContainerInvalidPublishCallbackException;
import io.valkyrja.tests.fixtures.container.provider.InvalidDeferredProviderFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedFixture;
import io.valkyrja.tests.fixtures.container.provider.ProvidedSecondaryFixture;
import io.valkyrja.tests.fixtures.container.provider.ProviderFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercised through the concrete {@link Container}, which extends {@code ProvidersAware}. */
final class ProvidersAwareTest {

    @BeforeEach
    void resetFlags() {
        ProviderFixture.publishCalled = false;
        ProviderFixture.publishSecondaryCalled = false;
    }

    @Test
    void register() {
        var aware = new Container();

        assertFalse(ProviderFixture.publishCalled);
        assertFalse(ProviderFixture.publishSecondaryCalled);
        assertFalse(aware.isPublished(ProvidedFixture.class));
        assertFalse(aware.isPublished(ProvidedSecondaryFixture.class));

        aware.register(new ProviderFixture());
        // Re-registering the same provider just overwrites the callbacks.
        aware.register(new ProviderFixture());

        assertFalse(ProviderFixture.publishCalled);
        assertFalse(aware.isPublished(ProvidedFixture.class));

        aware.publish(ProvidedFixture.class);

        assertTrue(ProviderFixture.publishCalled);
        assertFalse(ProviderFixture.publishSecondaryCalled);
        assertTrue(aware.isPublished(ProvidedFixture.class));
        assertFalse(aware.isPublished(ProvidedSecondaryFixture.class));

        aware.publish(ProvidedSecondaryFixture.class);

        assertTrue(ProviderFixture.publishSecondaryCalled);
        assertTrue(aware.isPublished(ProvidedSecondaryFixture.class));
    }

    @Test
    void publishBeforeRegisterIsNoOp() {
        var aware = new Container();

        aware.publish(ProvidedFixture.class);

        assertFalse(aware.isPublished(ProvidedFixture.class));
    }

    @Test
    void registerInvalidCallbackThrows() {
        var aware = new Container();

        assertThrows(
                ContainerInvalidPublishCallbackException.class,
                () -> aware.register(new InvalidDeferredProviderFixture()));
    }
}
