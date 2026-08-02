/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.ParsedBodyRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete parsed-body request struct selecting name and email fields. */
public final class ParsedBodyStructFixture extends ParsedBodyRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("name", "name", "email", "email");
    }

    @Override
    public List<String> values() {
        return List.of("name", "email");
    }
}
