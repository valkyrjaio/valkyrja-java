/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
