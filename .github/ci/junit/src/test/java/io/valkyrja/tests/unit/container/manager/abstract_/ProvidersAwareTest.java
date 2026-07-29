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
import io.valkyrja.tests.fixtures.container.provider.InvalidDeferredProviderClass;
import io.valkyrja.tests.fixtures.container.provider.ProvidedClass;
import io.valkyrja.tests.fixtures.container.provider.ProvidedSecondaryClass;
import io.valkyrja.tests.fixtures.container.provider.ProviderClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercised through the concrete {@link Container}, which extends {@code ProvidersAware}. */
final class ProvidersAwareTest {

    @BeforeEach
    void resetFlags() {
        ProviderClass.publishCalled = false;
        ProviderClass.publishSecondaryCalled = false;
    }

    @Test
    void register() {
        var aware = new Container();

        assertFalse(ProviderClass.publishCalled);
        assertFalse(ProviderClass.publishSecondaryCalled);
        assertFalse(aware.isPublished(ProvidedClass.class));
        assertFalse(aware.isPublished(ProvidedSecondaryClass.class));

        aware.register(new ProviderClass());
        // Re-registering the same provider just overwrites the callbacks.
        aware.register(new ProviderClass());

        assertFalse(ProviderClass.publishCalled);
        assertFalse(aware.isPublished(ProvidedClass.class));

        aware.publish(ProvidedClass.class);

        assertTrue(ProviderClass.publishCalled);
        assertFalse(ProviderClass.publishSecondaryCalled);
        assertTrue(aware.isPublished(ProvidedClass.class));
        assertFalse(aware.isPublished(ProvidedSecondaryClass.class));

        aware.publish(ProvidedSecondaryClass.class);

        assertTrue(ProviderClass.publishSecondaryCalled);
        assertTrue(aware.isPublished(ProvidedSecondaryClass.class));
    }

    @Test
    void publishBeforeRegisterIsNoOp() {
        var aware = new Container();

        aware.publish(ProvidedClass.class);

        assertFalse(aware.isPublished(ProvidedClass.class));
    }

    @Test
    void registerInvalidCallbackThrows() {
        var aware = new Container();

        assertThrows(
                ContainerInvalidPublishCallbackException.class,
                () -> aware.register(new InvalidDeferredProviderClass()));
    }
}
