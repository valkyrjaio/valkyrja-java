/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file.collection.contract;

import io.valkyrja.http.message.file.contract.UploadedFileContract;

import java.util.Map;

public interface UploadedFileCollectionContract {

    boolean has(String key);

    UploadedFileContract get(String key);

    Map<String, UploadedFileContract> getAll();

    Map<String, UploadedFileContract> getOnly(String... keys);

    Map<String, UploadedFileContract> getAllExcept(String... keys);

    UploadedFileCollectionContract with(Map<String, UploadedFileContract> collection);

    UploadedFileCollectionContract withAdded(Map<String, UploadedFileContract> collection);
}
