/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data.contract;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import java.util.List;
import java.util.function.Consumer;

public interface ConfigContract {
    String namespace();

    String dir();

    String version();

    String environment();

    boolean debugMode();

    String timezone();

    String key();

    String dataPath();

    String dataNamespace();

    List<ComponentProviderContract> providers();

    List<Consumer<ApplicationContract>> callbacks();
}
