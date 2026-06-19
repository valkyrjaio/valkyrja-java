/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.throwable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.middleware.throwable.exception.abstract_.CliMiddlewareInvalidArgumentException;
import io.valkyrja.cli.middleware.throwable.exception.abstract_.CliMiddlewareRuntimeException;
import io.valkyrja.cli.server.throwable.exception.abstract_.CliServerInvalidArgumentException;
import io.valkyrja.cli.server.throwable.exception.abstract_.CliServerRuntimeException;
import org.junit.jupiter.api.Test;

/** Test the abstract cli middleware/server throwable bases (no concrete subclasses exist). */
final class CliAbstractExceptionTest {

    private final IllegalStateException cause = new IllegalStateException("cause");

    @Test
    void cliMiddlewareInvalidArgumentException() {
        assertEquals("m", new CliMiddlewareInvalidArgumentException("m") {}.getMessage());
        assertSame(cause, new CliMiddlewareInvalidArgumentException("m", cause) {}.getCause());
    }

    @Test
    void cliMiddlewareRuntimeException() {
        assertEquals("m", new CliMiddlewareRuntimeException("m") {}.getMessage());
        assertSame(cause, new CliMiddlewareRuntimeException("m", cause) {}.getCause());
    }

    @Test
    void cliServerInvalidArgumentException() {
        assertEquals("m", new CliServerInvalidArgumentException("m") {}.getMessage());
        assertSame(cause, new CliServerInvalidArgumentException("m", cause) {}.getCause());
    }

    @Test
    void cliServerRuntimeException() {
        assertEquals("m", new CliServerRuntimeException("m") {}.getMessage());
        assertSame(cause, new CliServerRuntimeException("m", cause) {}.getCause());
    }
}