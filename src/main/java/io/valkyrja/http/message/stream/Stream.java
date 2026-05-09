/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream;

import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.stream.enum_.Mode;
import io.valkyrja.http.message.stream.enum_.ModeTranslation;
import io.valkyrja.http.message.stream.enum_.PhpWrapper;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Stream implements StreamContract {

    private final PhpWrapper wrapper;
    private final Mode mode;
    private final ModeTranslation modeTranslation;
    private ByteArrayOutputStream buffer;
    private int position;
    private boolean closed;

    public Stream() {
        this(PhpWrapper.memory, Mode.WRITE_READ_CREATE, ModeTranslation.BINARY_SAFE);
    }

    public Stream(PhpWrapper wrapper, Mode mode, ModeTranslation modeTranslation) {
        this.wrapper = wrapper;
        this.mode = mode;
        this.modeTranslation = modeTranslation;
        this.buffer = new ByteArrayOutputStream();
        this.position = 0;
        this.closed = false;
    }

    @Override
    public String toString() {
        if (closed || !isReadable()) {
            return "";
        }
        try {
            rewind();
            return getContents();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            buffer = new ByteArrayOutputStream();
            position = 0;
        }
    }

    @Override
    public Closeable detach() {
        Closeable resource = buffer::reset;
        closed = true;
        buffer = new ByteArrayOutputStream();
        position = 0;
        return resource;
    }

    @Override
    public int getSize() {
        return buffer.size();
    }

    @Override
    public int tell() {
        return position;
    }

    @Override
    public boolean eof() {
        return position >= buffer.size();
    }

    @Override
    public boolean isSeekable() {
        return !closed;
    }

    @Override
    public void seek(int offset, int whence) {
        byte[] bytes = buffer.toByteArray();
        int size = bytes.length;

        switch (whence) {
            case SEEK_SET -> position = offset;
            case SEEK_CUR -> position = position + offset;
            case SEEK_END -> position = size + offset;
        }

        if (position < 0) {
            position = 0;
        }
    }

    @Override
    public void rewind() {
        seek(0, SEEK_SET);
    }

    @Override
    public boolean isWritable() {
        return !closed && mode.isWriteable();
    }

    @Override
    public int write(String string) {
        if (string == null) {
            return -1;
        }
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        byte[] current = buffer.toByteArray();
        int size = current.length;

        if (position >= size) {
            buffer.write(bytes, 0, bytes.length);
        } else {
            ByteArrayOutputStream newBuffer = new ByteArrayOutputStream();
            newBuffer.write(current, 0, position);
            newBuffer.write(bytes, 0, bytes.length);
            int afterWrite = position + bytes.length;
            if (afterWrite < size) {
                newBuffer.write(current, afterWrite, size - afterWrite);
            }
            buffer = newBuffer;
        }

        position += bytes.length;
        return bytes.length;
    }

    @Override
    public boolean isReadable() {
        return !closed && mode.isReadable();
    }

    @Override
    public String read(int length) {
        byte[] bytes = buffer.toByteArray();
        int available = bytes.length - position;
        if (available <= 0) {
            return "";
        }
        int toRead = Math.min(length, available);
        String result = new String(bytes, position, toRead, StandardCharsets.UTF_8);
        position += toRead;
        return result;
    }

    @Override
    public String getContents() {
        byte[] bytes = buffer.toByteArray();
        int available = bytes.length - position;
        if (available <= 0) {
            return "";
        }
        String result = new String(bytes, position, available, StandardCharsets.UTF_8);
        position = bytes.length;
        return result;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("wrapper_type", wrapper != null ? wrapper.getValue() : null);
        meta.put("stream_type", "MEMORY");
        meta.put("mode", mode != null ? mode.getValue() + (modeTranslation != null ? modeTranslation.getValue() : "") : null);
        meta.put("unread_bytes", buffer.size() - position);
        meta.put("seekable", isSeekable());
        meta.put("uri", wrapper != null ? wrapper.getValue() : null);
        return meta;
    }

    @Override
    public Object getMetadataItem(String key) {
        return getMetadata().get(key);
    }
}
