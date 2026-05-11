/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.file.collection;

import io.valkyrja.http.message.file.collection.contract.UploadedFileCollectionContract;
import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidKeyException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidParamException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UploadedFileCollection implements UploadedFileCollectionContract {

    protected Map<String, UploadedFileContract> files = new LinkedHashMap<>();

    public UploadedFileCollection(Map<String, UploadedFileContract> files) {
        this.files = new LinkedHashMap<>(files);
    }

    public static UploadedFileCollection fromArray(Map<String, UploadedFileContract> data) {
        for (UploadedFileContract value : data.values()) {
            validateFile(value);
        }
        return new UploadedFileCollection(data);
    }

    protected static void validateFile(UploadedFileContract file) {
        if (file == null) {
            throw new UploadedFileInvalidParamException("File must be an UploadedFileContract instance");
        }
    }

    @Override
    public boolean has(String key) {
        return files.containsKey(key);
    }

    @Override
    public UploadedFileContract get(String key) {
        if (!files.containsKey(key)) {
            throw new UploadedFileInvalidKeyException("The provided key '" + key + "' does not exist in the collection");
        }
        return files.get(key);
    }

    @Override
    public Map<String, UploadedFileContract> getAll() {
        return new LinkedHashMap<>(files);
    }

    @Override
    public Map<String, UploadedFileContract> getOnly(String... keys) {
        Set<String> keySet = Arrays.stream(keys).collect(Collectors.toSet());
        return files.entrySet().stream()
                .filter(e -> keySet.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    @Override
    public Map<String, UploadedFileContract> getAllExcept(String... keys) {
        Set<String> keySet = Arrays.stream(keys).collect(Collectors.toSet());
        return files.entrySet().stream()
                .filter(e -> !keySet.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    @Override
    public UploadedFileCollectionContract with(Map<String, UploadedFileContract> collection) {
        UploadedFileCollection copy = new UploadedFileCollection(files);
        copy.files = new LinkedHashMap<>(collection);
        return copy;
    }

    @Override
    public UploadedFileCollectionContract withAdded(Map<String, UploadedFileContract> collection) {
        UploadedFileCollection copy = new UploadedFileCollection(files);
        copy.files.putAll(collection);
        return copy;
    }
}
