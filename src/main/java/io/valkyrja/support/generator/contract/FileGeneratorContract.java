/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.support.generator.contract;

public interface FileGeneratorContract {

    String generateFileContents();

    void generateFile();
}
