/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.event.provider.contract;

import io.valkyrja.event.data.contract.ListenerContract;
import java.util.List;

/**
 * Contract for event listener providers.
 *
 * <p>PHP: static methods {@code getListenerClasses()} and {@code getListeners()}. Java uses
 * instance methods since interface static methods cannot be overridden.
 */
public interface ListenerProviderContract {

    /**
     * Get a list of classes annotated with listener attributes.
     *
     * @return list of listener class references
     */
    List<Class<?>> getListenerClasses();

    /**
     * Get a list of explicitly-configured listeners.
     *
     * @return list of listener data objects
     */
    List<ListenerContract> getListeners();
}
