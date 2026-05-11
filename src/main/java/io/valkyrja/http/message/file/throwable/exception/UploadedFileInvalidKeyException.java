/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file.throwable.exception;

import io.valkyrja.http.message.file.throwable.exception.abstract_.UploadedFileInvalidArgumentException;

public class UploadedFileInvalidKeyException extends UploadedFileInvalidArgumentException {

    public UploadedFileInvalidKeyException(String message) {
        super(message);
    }

    public UploadedFileInvalidKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
