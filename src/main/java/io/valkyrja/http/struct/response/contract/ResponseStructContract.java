/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.response.contract;

import io.valkyrja.http.struct.contract.StructContract;
import java.util.Map;

public interface ResponseStructContract extends StructContract {

    Map<String, Object> getStructuredData(Map<String, Object> data, boolean includeAll);
}
