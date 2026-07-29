/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.file.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.file.UploadedFile;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidKeyException;
import io.valkyrja.http.message.file.throwable.exception.UploadedFileInvalidParamException;
import io.valkyrja.http.message.stream.Stream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link UploadedFileCollection}. */
final class UploadedFileCollectionTest {

    private static UploadedFile file() {
        var stream = new Stream();
        stream.write("data");
        return new UploadedFile(null, stream, 4, "f.txt", "text/plain");
    }

    private static UploadedFileCollection collection() {
        return new UploadedFileCollection(Map.of("avatar", file(), "doc", file()));
    }

    @Test
    void hasGetAndGetAll() {
        var collection = collection();

        assertTrue(collection.has("avatar"));
        assertFalse(collection.has("missing"));
        assertEquals(2, collection.getAll().size());
        assertEquals("f.txt", collection.get("avatar").getClientFilename());
    }

    @Test
    void getThrowsForMissingKey() {
        assertThrows(UploadedFileInvalidKeyException.class, () -> collection().get("missing"));
    }

    @Test
    void getOnlyAndGetAllExcept() {
        var collection = collection();

        assertEquals(1, collection.getOnly("avatar").size());
        assertEquals(1, collection.getAllExcept("avatar").size());
    }

    @Test
    void withAndWithAdded() {
        var collection = collection();

        assertEquals(1, collection.with(Map.of("only", file())).getAll().size());
        assertEquals(3, collection.withAdded(Map.of("extra", file())).getAll().size());
    }

    @Test
    void fromArrayValidatesEntries() {
        assertTrue(UploadedFileCollection.fromArray(Map.of("a", file())).has("a"));

        Map<String, UploadedFileContract> withNull = new HashMap<>();
        withNull.put("bad", null);
        assertThrows(
                UploadedFileInvalidParamException.class,
                () -> UploadedFileCollection.fromArray(withNull));
    }
}
