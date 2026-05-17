/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.support.generator.abstract_;

import io.valkyrja.support.generator.contract.FileGeneratorContract;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class FileGenerator implements FileGeneratorContract {

    protected String filePath;

    public FileGenerator(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void generateFile() {
        String contents = generateFileContents();
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(Objects.requireNonNullElse(path.getParent(), Paths.get(".")));
            Files.writeString(path, contents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + filePath, e);
        }
    }
}
