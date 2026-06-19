/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream.factory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.factory.StreamFactory;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamInvalidStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamReadException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamSeekException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamTellException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamWriteException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnreadableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnseekableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnwritableStreamException;
import org.junit.jupiter.api.Test;

/** Test the {@link StreamFactory}. */
final class StreamFactoryTest {

    @Test
    void isModeWriteable() {
        assertTrue(StreamFactory.isModeWriteable("w"));
        assertTrue(StreamFactory.isModeWriteable("r+"));
        assertFalse(StreamFactory.isModeWriteable("r"));
        assertFalse(StreamFactory.isModeWriteable(null));
    }

    @Test
    void isModeReadable() {
        assertTrue(StreamFactory.isModeReadable("r"));
        assertTrue(StreamFactory.isModeReadable("w+"));
        assertFalse(StreamFactory.isModeReadable("w"));
        assertFalse(StreamFactory.isModeReadable(null));
    }

    @Test
    void toStringReadsContents() {
        var stream = new Stream();
        stream.write("hello");

        assertEquals("hello", StreamFactory.toString(stream));
        assertEquals("", StreamFactory.toString(null));
    }

    @Test
    void verifyCapabilitiesThrowWhenClosed() {
        var closed = new Stream();
        closed.close();

        assertThrows(
                HttpStreamUnwritableStreamException.class,
                () -> StreamFactory.verifyWritable(closed));
        assertThrows(
                HttpStreamUnseekableStreamException.class,
                () -> StreamFactory.verifySeekable(closed));
        assertThrows(
                HttpStreamUnreadableStreamException.class,
                () -> StreamFactory.verifyReadable(closed));
    }

    @Test
    void verifyCapabilitiesPassForOpenStream() {
        var stream = new Stream();

        assertDoesNotThrow(() -> StreamFactory.verifyWritable(stream));
        assertDoesNotThrow(() -> StreamFactory.verifySeekable(stream));
        assertDoesNotThrow(() -> StreamFactory.verifyReadable(stream));
    }

    @Test
    void verifyResults() {
        assertThrows(
                HttpStreamStreamSeekException.class, () -> StreamFactory.verifySeekResult(1));
        assertThrows(
                HttpStreamStreamWriteException.class, () -> StreamFactory.verifyWriteResult(-1));
        assertThrows(
                HttpStreamStreamReadException.class, () -> StreamFactory.verifyReadResult(null));
        assertThrows(
                HttpStreamStreamTellException.class, () -> StreamFactory.verifyTellResult(-1));
        assertDoesNotThrow(() -> StreamFactory.verifySeekResult(0));
        assertDoesNotThrow(() -> StreamFactory.verifyReadResult("ok"));
        assertDoesNotThrow(() -> StreamFactory.verifyWriteResult(5));
        assertDoesNotThrow(() -> StreamFactory.verifyTellResult(5));
    }

    @Test
    void validateStream() {
        assertThrows(
                HttpStreamInvalidStreamException.class, () -> StreamFactory.validateStream(null));
        assertDoesNotThrow(() -> StreamFactory.validateStream(new Object()));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new StreamFactory() {});
    }
}