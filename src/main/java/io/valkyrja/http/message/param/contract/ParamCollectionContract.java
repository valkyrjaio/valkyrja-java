/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param.contract;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface ParamCollectionContract {

    boolean has(String key);

    @Nullable Object get(String key);

    Map<String, Object> getAll();

    Map<String, Object> getOnly(String... keys);

    Map<String, Object> getAllExcept(String... keys);

    ParamCollectionContract with(Map<String, Object> params);

    ParamCollectionContract withAdded(Map<String, Object> params);
}
