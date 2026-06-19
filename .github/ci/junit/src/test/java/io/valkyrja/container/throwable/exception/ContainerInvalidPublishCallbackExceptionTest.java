/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

final class ContainerInvalidPublishCallbackExceptionTest {

    @Test
    void message() {
        var exception = new ContainerInvalidPublishCallbackException("bad callback");

        assertEquals("bad callback", exception.getMessage());
    }

    @Test
    void messageWithCause() {
        var cause = new RuntimeException("root cause");

        var exception = new ContainerInvalidPublishCallbackException("bad callback", cause);

        assertEquals("bad callback", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
