/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.stream.enum_.Mode;
import io.valkyrja.http.message.stream.enum_.ModeTranslation;
import io.valkyrja.http.message.stream.enum_.PhpWrapper;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamSeekException;
import org.junit.jupiter.api.Test;

/** Test the in-memory {@link Stream}. */
final class StreamTest {

    @Test
    void writeReadAndContents() {
        var stream = new Stream();

        assertEquals(5, stream.write("hello"));
        assertEquals(5, stream.getSize());
        assertEquals(5, stream.tell());
        assertTrue(stream.eof());

        stream.rewind();
        assertFalse(stream.eof());
        assertEquals("he", stream.read(2));
        assertEquals(2, stream.tell());
        assertEquals("llo", stream.getContents());
        assertEquals("", stream.read(10));
        assertEquals("", stream.getContents());
    }

    @Test
    void seekWhences() {
        var stream = new Stream();
        stream.write("hello");

        stream.seek(1, StreamContract.SEEK_SET);
        assertEquals(1, stream.tell());
        stream.seek(2, StreamContract.SEEK_CUR);
        assertEquals(3, stream.tell());
        stream.seek(-1, StreamContract.SEEK_END);
        assertEquals(4, stream.tell());
        stream.seek(-100, StreamContract.SEEK_SET);
        assertEquals(0, stream.tell());

        assertThrows(HttpStreamStreamSeekException.class, () -> stream.seek(0, 99));
    }

    @Test
    void writeOverwritesAtPosition() {
        var stream = new Stream();
        stream.write("hello");
        stream.seek(0, StreamContract.SEEK_SET);
        stream.write("XY");

        stream.rewind();
        assertEquals("XYllo", stream.getContents());
    }

    @Test
    void writeNullReturnsMinusOne() {
        assertEquals(-1, new Stream().write(null));
    }

    @Test
    void toStringReturnsContents() {
        var stream = new Stream();
        stream.write("hello");

        assertEquals("hello", stream.toString());
    }

    @Test
    void toStringSwallowsErrorsAndReturnsEmpty() {
        var stream = new Stream() {
            @Override
            public void rewind() {
                throw new RuntimeException("boom");
            }
        };
        stream.write("hello");

        assertEquals("", stream.toString());
    }

    @Test
    void closeResetsAndDisablesStream() {
        var stream = new Stream();
        stream.write("hello");

        stream.close();

        assertFalse(stream.isSeekable());
        assertFalse(stream.isReadable());
        assertFalse(stream.isWritable());
        assertEquals("", stream.toString());
    }

    @Test
    void detachClosesStream() {
        var stream = new Stream();
        stream.write("hi");

        stream.detach();

        assertFalse(stream.isSeekable());
        assertEquals(0, stream.getSize());
    }

    @Test
    void readWriteModesControlCapabilities() {
        var readOnly = new Stream(PhpWrapper.memory, Mode.READ, ModeTranslation.BINARY_SAFE);
        assertTrue(readOnly.isReadable());
        assertFalse(readOnly.isWritable());

        var writeOnly = new Stream(PhpWrapper.memory, Mode.WRITE, ModeTranslation.BINARY_SAFE);
        assertFalse(writeOnly.isReadable());
        assertTrue(writeOnly.isWritable());
    }

    @Test
    void metadata() {
        var stream = new Stream();
        stream.write("hi");

        var meta = stream.getMetadata();
        assertEquals("MEMORY", meta.get("stream_type"));
        assertEquals(true, meta.get("seekable"));
        assertEquals("MEMORY", stream.getMetadataItem("stream_type"));
    }

    @Test
    void metadataHandlesNullWrapperModeAndTranslation() {
        var meta = new Stream(null, null, null).getMetadata();

        assertNull(meta.get("wrapper_type"));
        assertNull(meta.get("mode"));
        assertNull(meta.get("uri"));
    }

    @Test
    void closeIsIdempotent() {
        var stream = new Stream();
        stream.close();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(stream::close);
    }

    @Test
    void toStringReturnsEmptyForNonReadableStream() {
        var stream = new Stream(PhpWrapper.memory, Mode.WRITE, ModeTranslation.BINARY_SAFE);

        assertEquals("", stream.toString());
    }

}
