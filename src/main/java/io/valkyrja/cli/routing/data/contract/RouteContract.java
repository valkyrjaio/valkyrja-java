/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data.contract;

import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ExitedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface RouteContract {

    String getName();

    RouteContract withName(String name);

    String getDescription();

    RouteContract withDescription(String description);

    boolean hasHelpText();

    Supplier<MessageContract> getHelpText();

    MessageContract getHelpTextMessage();

    RouteContract withHelpText(Supplier<MessageContract> helpText);

    boolean hasArguments();

    List<ArgumentParameterContract> getArguments();

    boolean hasArgument(String name);

    ArgumentParameterContract getArgument(String name);

    RouteContract withArguments(ArgumentParameterContract... arguments);

    RouteContract withAddedArguments(ArgumentParameterContract... arguments);

    boolean hasOptions();

    List<OptionParameterContract> getOptions();

    boolean hasOption(String name);

    OptionParameterContract getOption(String name);

    RouteContract withOptions(OptionParameterContract... options);

    RouteContract withAddedOptions(OptionParameterContract... options);

    List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware();

    RouteContract withRouteMatchedMiddleware(List<Class<? extends RouteMatchedMiddlewareContract>> middleware);

    RouteContract withAddedRouteMatchedMiddleware(List<Class<? extends RouteMatchedMiddlewareContract>> middleware);

    List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware();

    RouteContract withRouteDispatchedMiddleware(List<Class<? extends RouteDispatchedMiddlewareContract>> middleware);

    RouteContract withAddedRouteDispatchedMiddleware(List<Class<? extends RouteDispatchedMiddlewareContract>> middleware);

    List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware();

    RouteContract withThrowableCaughtMiddleware(List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware);

    RouteContract withAddedThrowableCaughtMiddleware(List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware);

    List<Class<? extends ExitedMiddlewareContract>> getExitedMiddleware();

    RouteContract withExitedMiddleware(List<Class<? extends ExitedMiddlewareContract>> middleware);

    RouteContract withAddedExitedMiddleware(List<Class<? extends ExitedMiddlewareContract>> middleware);

    BiFunction<ContainerContract, RouteContract, OutputContract> getHandler();

    RouteContract withHandler(BiFunction<ContainerContract, RouteContract, OutputContract> handler);
}
