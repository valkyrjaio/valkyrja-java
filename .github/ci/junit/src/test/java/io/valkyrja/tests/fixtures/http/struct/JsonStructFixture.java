/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.JsonRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete JSON request struct selecting the name field. */
public final class JsonStructFixture extends JsonRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("name", "name");
    }

    @Override
    public List<String> values() {
        return List.of("name");
    }
}
