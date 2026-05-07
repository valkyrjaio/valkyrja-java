/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
