/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.data.contract;

import java.util.Map;

public interface EventDataContract {
    Map<Class<?>, Map<String, String>> events();

    Map<String, ListenerContract> listeners();
}
