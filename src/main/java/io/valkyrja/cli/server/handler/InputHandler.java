/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.handler;

import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.ExitedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.server.handler.contract.InputHandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class InputHandler implements InputHandlerContract {

    protected final ContainerContract container;
    protected final RouterContract router;
    protected final InputReceivedHandlerContract inputReceivedHandler;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final ExitedHandlerContract exitedHandler;
    protected final CliInteractionConfigContract interactionConfig;

    public InputHandler(
            ContainerContract container,
            RouterContract router,
            InputReceivedHandlerContract inputReceivedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            ExitedHandlerContract exitedHandler,
            CliInteractionConfigContract interactionConfig) {
        this.container = container;
        this.router = router;
        this.inputReceivedHandler = inputReceivedHandler;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.exitedHandler = exitedHandler;
        this.interactionConfig = interactionConfig;
    }

    @Override
    public OutputContract handle(InputContract input) {
        OutputContract output;
        try {
            output = dispatchRouter(input);
        } catch (Throwable throwable) {
            output = throwableCaughtHandler.throwableCaught(input, emptyOutput(), throwable);
        }
        container.setSingleton(OutputContract.class, output);
        return output;
    }

    @Override
    public void exit(InputContract input, OutputContract output) {
        exitedHandler.exited(input, output);
    }

    @Override
    public void run(InputContract input) {
        OutputContract output = handle(input);
        output.writeMessages();
        exit(input, output);
        Object exitCode = output.getExitCode();
        int code = exitCode instanceof ExitCode ec ? ec.value : (int) exitCode;
        System.exit(code);
    }

    protected OutputContract dispatchRouter(InputContract input) {
        container.setSingleton(InputContract.class, input);
        Object afterMiddleware = inputReceivedHandler.inputReceived(input);
        if (afterMiddleware instanceof OutputContract earlyOutput) {
            return earlyOutput;
        }
        InputContract processedInput = (InputContract) afterMiddleware;
        container.setSingleton(InputContract.class, processedInput);
        return router.dispatch(processedInput);
    }

    protected OutputContract emptyOutput() {
        return null;
    }
}
