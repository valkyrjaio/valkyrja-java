/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.event.provider;

import io.valkyrja.event.data.contract.ListenerContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import java.util.List;

/** Test event listener provider with no listeners. */
public final class ListenerProviderFixture implements ListenerProviderContract {

    @Override
    public List<Class<?>> getListenerClasses() {
        return List.of();
    }

    @Override
    public List<ListenerContract> getListeners() {
        return List.of();
    }
}
