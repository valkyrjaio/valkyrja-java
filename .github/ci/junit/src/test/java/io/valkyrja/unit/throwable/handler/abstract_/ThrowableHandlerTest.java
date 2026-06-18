/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.throwable.handler.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import io.valkyrja.throwable.exception.RuntimeException;
import io.valkyrja.throwable.handler.abstract_.ThrowableHandler;
import io.valkyrja.throwable.handler.contract.ThrowableHandlerContract;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

/** Test the {@link ThrowableHandler} trace-code generator. */
final class ThrowableHandlerTest {

    @Test
    void getTraceCodeReturnsMd5Hex() {
        var code = ThrowableHandler.getTraceCode(new java.lang.RuntimeException("boom"));

        assertEquals(32, code.length());
        assertTrue(code.matches("[0-9a-f]{32}"));
    }

    @Test
    void getTraceCodeIsDeterministicForSameThrowable() {
        var throwable = new java.lang.RuntimeException("boom");

        assertEquals(
                ThrowableHandler.getTraceCode(throwable),
                ThrowableHandler.getTraceCode(throwable));
    }

    @Test
    void contractDefaultStaticThrows() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> ThrowableHandlerContract.getTraceCode(new java.lang.RuntimeException()));
    }

    @Test
    void abstractClassIsInstantiableBySubclass() {
        var instance = new ThrowableHandler() {};

        assertInstanceOf(ThrowableHandler.class, instance);
    }

    @Test
    void getTraceCodeWrapsMissingMd5Algorithm() {
        try (var mocked = mockStatic(MessageDigest.class)) {
            mocked.when(() -> MessageDigest.getInstance("MD5"))
                    .thenThrow(new NoSuchAlgorithmException("no md5"));

            var thrown =
                    assertThrows(
                            RuntimeException.class,
                            () -> ThrowableHandler.getTraceCode(new java.lang.RuntimeException()));

            assertTrue(thrown.getMessage().contains("MD5 algorithm not available"));
        }
    }
}