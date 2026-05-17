/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file.factory;

import io.valkyrja.http.message.file.UploadedFile;
import io.valkyrja.http.message.stream.contract.StreamContract;

public abstract class UploadedFileFactory {

    public static UploadedFile createFromFile(
            String file, int size, String fileName, String mediaType) {
        return new UploadedFile(file, null, size, fileName, mediaType);
    }

    public static UploadedFile createFromStream(
            StreamContract stream, int size, String fileName, String mediaType) {
        return new UploadedFile(null, stream, size, fileName, mediaType);
    }
}
