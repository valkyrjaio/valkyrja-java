/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.container.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.container.throwable.exception.*;
import org.junit.jupiter.api.Test;

final class ContainerInvalidReferenceExceptionTest {

    @Test
    void message() {
        String id = ContainerInvalidReferenceExceptionTest.class.getName();

        var exception = new ContainerInvalidReferenceException(id);

        assertEquals("Service with `" + id + "` not found", exception.getMessage());
    }

    @Test
    void messageWithCause() {
        String id = ContainerInvalidReferenceExceptionTest.class.getName();
        var cause = new RuntimeException("root cause");

        var exception = new ContainerInvalidReferenceException(id, cause);

        assertEquals("Service with `" + id + "` not found", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
