/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.server.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.server.generator.ResponseFileGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the {@link ResponseFileGenerator}. */
final class ResponseFileGeneratorTest {

    @Test
    void generateFileContentsSerializesResponse() {
        var response =
                Response.create(
                        "hello body",
                        StatusCode.OK,
                        new HeaderCollection(new Header("X-Test", "v")));
        var generator = new ResponseFileGenerator(response, "/tmp/unused");

        var contents = generator.generateFileContents();

        assertTrue(contents.contains("statusCode=200"));
        assertTrue(contents.contains("body=hello body"));
        assertTrue(contents.contains("header.x-test=v"));
    }

    @Test
    void generateFileWritesToDisk(@TempDir Path dir) throws IOException {
        var response = Response.create("payload", StatusCode.OK, new HeaderCollection());
        Path target = dir.resolve("response.cache");

        new ResponseFileGenerator(response, target.toString()).generateFile();

        assertTrue(Files.readString(target).contains("body=payload"));
    }
}
