/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.support.generator;

import io.valkyrja.support.generator.abstract_.FileGenerator;

/** Concrete {@link FileGenerator} that writes fixed contents. */
public final class FileGeneratorClass extends FileGenerator {

    public static final String CONTENTS = "generated contents";

    public FileGeneratorClass(String filePath) {
        super(filePath);
    }

    @Override
    public String generateFileContents() {
        return CONTENTS;
    }
}
