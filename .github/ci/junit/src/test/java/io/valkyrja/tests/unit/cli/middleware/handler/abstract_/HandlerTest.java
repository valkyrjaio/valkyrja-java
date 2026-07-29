/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.middleware.handler.abstract_;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.middleware.handler.abstract_.Handler;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.Test;

/** Test the {@link Handler}. */
final class HandlerTest {

    /** A minimal concrete handler exposing the protected chain-advancing seam. */
    private static final class TestHandler extends Handler<Runnable> {
        @SafeVarargs
        TestHandler(Container container, Class<? extends Runnable>... middleware) {
            super(container, middleware);
        }

        Runnable advance() {
            Class<? extends Runnable> current = next;

            return current != null ? getMiddleware(current) : null;
        }
    }

    private static final class Noop implements Runnable {
        @Override
        public void run() {}
    }

    @Test
    void advancesThroughRegisteredMiddlewareAndAddsMore() {
        var container = new Container();
        var noop = new Noop();
        container.setSingleton(Noop.class, noop);

        var handler = new TestHandler(container);
        assertNull(handler.advance());

        handler.add(Noop.class);
        assertSame(noop, handler.advance());
        // Index advanced past the only entry.
        assertNull(handler.advance());
    }
}
