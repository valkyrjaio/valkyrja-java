/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.application.throwable.contract.ApplicationThrowable;
import io.valkyrja.application.throwable.exception.abstract_.ApplicationInvalidArgumentException;
import io.valkyrja.application.throwable.exception.abstract_.ApplicationRuntimeException;
import org.junit.jupiter.api.Test;

final class ApplicationExceptionTest {

    @Test
    void runtimeExceptionExposesMessageAndCause() {
        var cause = new IllegalStateException("root");
        var withMessage = new ApplicationRuntimeException("boom") {};
        var withCause = new ApplicationRuntimeException("boom", cause) {};

        assertEquals("boom", withMessage.getMessage());
        assertEquals("boom", withCause.getMessage());
        assertSame(cause, withCause.getCause());
        assertInstanceOf(ApplicationThrowable.class, withMessage);
    }

    @Test
    void invalidArgumentExceptionExposesMessageAndCause() {
        var cause = new IllegalStateException("root");
        var withMessage = new ApplicationInvalidArgumentException("bad") {};
        var withCause = new ApplicationInvalidArgumentException("bad", cause) {};

        assertEquals("bad", withMessage.getMessage());
        assertEquals("bad", withCause.getMessage());
        assertSame(cause, withCause.getCause());
        assertInstanceOf(ApplicationThrowable.class, withMessage);
    }
}
