/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.middleware.inputreceived;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;

public class CheckGlobalInteractionOptionsMiddleware implements InputReceivedMiddlewareContract {

    protected CliInteractionConfig config;
    protected String noInteractionOptionName;
    protected String noInteractionOptionShortName;
    protected String quietOptionName;
    protected String quietOptionShortName;
    protected String silentOptionName;
    protected String silentOptionShortName;

    public CheckGlobalInteractionOptionsMiddleware(
            CliInteractionConfig config,
            String noInteractionOptionName,
            String noInteractionOptionShortName,
            String quietOptionName,
            String quietOptionShortName,
            String silentOptionName,
            String silentOptionShortName) {
        this.config = config;
        this.noInteractionOptionName = noInteractionOptionName;
        this.noInteractionOptionShortName = noInteractionOptionShortName;
        this.quietOptionName = quietOptionName;
        this.quietOptionShortName = quietOptionShortName;
        this.silentOptionName = silentOptionName;
        this.silentOptionShortName = silentOptionShortName;
    }

    @Override
    public Object inputReceived(InputContract input, InputReceivedHandlerContract handler) {
        setIsInteractive(input);
        setIsQuiet(input);
        setIsSilent(input);
        return handler.inputReceived(input);
    }

    protected void setIsInteractive(InputContract input) {
        if (input.hasOption(noInteractionOptionShortName)
                || input.hasOption(noInteractionOptionName)) {
            config.setInteractive(false);
        }
    }

    protected void setIsQuiet(InputContract input) {
        if (input.hasOption(quietOptionShortName) || input.hasOption(quietOptionName)) {
            config.setQuiet(true);
        }
    }

    protected void setIsSilent(InputContract input) {
        if (input.hasOption(silentOptionShortName) || input.hasOption(silentOptionName)) {
            config.setSilent(true);
        }
    }
}
