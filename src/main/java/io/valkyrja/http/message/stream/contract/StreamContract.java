/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.stream.contract;

import java.io.Closeable;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface StreamContract {

    int SEEK_SET = 0;
    int SEEK_CUR = 1;
    int SEEK_END = 2;

    String toString();

    void close();

    Closeable detach();

    int getSize();

    int tell();

    boolean eof();

    boolean isSeekable();

    void seek(int offset, int whence);

    void rewind();

    boolean isWritable();

    int write(String string);

    boolean isReadable();

    String read(int length);

    String getContents();

    Map<String, Object> getMetadata();

    @Nullable Object getMetadataItem(String key);
}
