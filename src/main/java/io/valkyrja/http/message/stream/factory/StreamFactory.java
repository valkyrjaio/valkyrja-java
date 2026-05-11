/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream.factory;

import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamInvalidStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamReadException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamSeekException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamTellException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamStreamWriteException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnreadableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnseekableStreamException;
import io.valkyrja.http.message.stream.throwable.exception.HttpStreamUnwritableStreamException;

public abstract class StreamFactory {

    public static boolean isModeWriteable(String mode) {
        if (mode == null) {
            return false;
        }
        for (char c : mode.toCharArray()) {
            if (c == 'w' || c == 'x' || c == 'c' || c == 'a' || c == '+') {
                return true;
            }
        }
        return false;
    }

    public static boolean isModeReadable(String mode) {
        if (mode == null) {
            return false;
        }
        for (char c : mode.toCharArray()) {
            if (c == 'r' || c == '+') {
                return true;
            }
        }
        return false;
    }

    public static String toString(StreamContract stream) {
        if (stream == null || !stream.isReadable()) {
            return "";
        }
        stream.rewind();
        return stream.getContents();
    }

    public static void verifyWritable(StreamContract stream) {
        if (!stream.isWritable()) {
            throw new HttpStreamUnwritableStreamException("Stream is not writable.");
        }
    }

    public static void verifySeekable(StreamContract stream) {
        if (!stream.isSeekable()) {
            throw new HttpStreamUnseekableStreamException("Stream is not seekable.");
        }
    }

    public static void verifyReadable(StreamContract stream) {
        if (!stream.isReadable()) {
            throw new HttpStreamUnreadableStreamException("Stream is not readable.");
        }
    }

    public static void verifySeekResult(int result) {
        if (result != 0) {
            throw new HttpStreamStreamSeekException("Stream seek failed.");
        }
    }

    public static void verifyWriteResult(int result) {
        if (result == -1) {
            throw new HttpStreamStreamWriteException("Stream write failed.");
        }
    }

    public static void verifyReadResult(String result) {
        if (result == null) {
            throw new HttpStreamStreamReadException("Stream read failed.");
        }
    }

    public static void verifyTellResult(int result) {
        if (result == -1) {
            throw new HttpStreamStreamTellException("Stream tell failed.");
        }
    }

    public static void validateStream(Object resource) {
        if (resource == null) {
            throw new HttpStreamInvalidStreamException("Invalid stream resource.");
        }
    }
}
