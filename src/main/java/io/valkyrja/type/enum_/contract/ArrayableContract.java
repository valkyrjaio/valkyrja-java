/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.type.enum_.contract;

import java.util.List;
import java.util.Map;

public interface ArrayableContract {

    Map<String, String> asMap();

    List<String> values();
}
