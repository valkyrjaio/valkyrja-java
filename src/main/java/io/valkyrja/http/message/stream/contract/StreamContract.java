/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
