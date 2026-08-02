/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.url.contract;

import java.util.Map;

public interface UrlContract {

    String getUrl(String name, Map<String, Object> data);
}
