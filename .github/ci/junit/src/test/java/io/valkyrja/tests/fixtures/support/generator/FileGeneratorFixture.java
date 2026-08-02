/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.support.generator;

import io.valkyrja.support.generator.abstract_.FileGenerator;

/** Concrete {@link FileGenerator} that writes fixed contents. */
public final class FileGeneratorFixture extends FileGenerator {

    public static final String CONTENTS = "generated contents";

    public FileGeneratorFixture(String filePath) {
        super(filePath);
    }

    @Override
    public String generateFileContents() {
        return CONTENTS;
    }
}
