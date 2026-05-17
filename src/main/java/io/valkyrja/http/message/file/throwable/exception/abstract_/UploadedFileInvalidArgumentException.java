/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file.throwable.exception.abstract_;

import io.valkyrja.http.message.file.throwable.contract.UploadedFileThrowable;
import io.valkyrja.http.message.throwable.exception.abstract_.HttpMessageInvalidArgumentException;

public abstract class UploadedFileInvalidArgumentException
        extends HttpMessageInvalidArgumentException implements UploadedFileThrowable {

    protected UploadedFileInvalidArgumentException(String message) {
        super(message);
    }

    protected UploadedFileInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
