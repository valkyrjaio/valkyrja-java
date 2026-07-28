/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.dispatcher;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Router implements RouterContract {

    protected final ContainerContract container;
    protected final RouteCollectionContract collection;
    protected final OutputFactoryContract outputFactory;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final RouteMatchedHandlerContract routeMatchedHandler;
    protected final RouteNotMatchedHandlerContract routeNotMatchedHandler;
    protected final RouteDispatchedHandlerContract routeDispatchedHandler;
    protected final ProcessExitingHandlerContract processExitingHandler;

    public Router(
            ContainerContract container,
            RouteCollectionContract collection,
            OutputFactoryContract outputFactory,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            RouteMatchedHandlerContract routeMatchedHandler,
            RouteNotMatchedHandlerContract routeNotMatchedHandler,
            RouteDispatchedHandlerContract routeDispatchedHandler,
            ProcessExitingHandlerContract processExitingHandler) {
        this.container = container;
        this.collection = collection;
        this.outputFactory = outputFactory;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.routeMatchedHandler = routeMatchedHandler;
        this.routeNotMatchedHandler = routeNotMatchedHandler;
        this.routeDispatchedHandler = routeDispatchedHandler;
        this.processExitingHandler = processExitingHandler;
    }

    @Override
    public OutputContract dispatch(InputContract input) {
        String commandName = input.getCommandName();

        if (!collection.has(commandName)) {
            OutputContract notFoundOutput = outputFactory.createOutput();
            return routeNotMatchedHandler.routeNotMatched(input, notFoundOutput);
        }

        RouteContract route = addParametersToRoute(input, collection.get(commandName));

        // The command has been matched
        routeMatched(route);

        Object afterMatched = routeMatchedHandler.routeMatched(input, route);

        if (afterMatched instanceof OutputContract earlyOutput) {
            return earlyOutput;
        }

        RouteContract matchedRoute = (RouteContract) afterMatched;

        // Set the command after middleware has potentially modified it in the service container
        container.setSingleton(RouteContract.class, matchedRoute);

        OutputContract output = matchedRoute.getHandler().apply(container, matchedRoute);

        return routeDispatchedHandler.routeDispatched(input, output, matchedRoute);
    }

    /** Add the parameters from the input to the route. */
    protected RouteContract addParametersToRoute(InputContract input, RouteContract route) {
        return addOptionsToRoute(input, addArgumentsToRoute(input, route));
    }

    /** Add the arguments from the input to the route. */
    protected RouteContract addArgumentsToRoute(InputContract input, RouteContract route) {
        Map<Integer, ArgumentContract> arguments = new LinkedHashMap<>();
        List<ArgumentContract> inputArguments = input.getArguments();

        for (int index = 0; index < inputArguments.size(); index++) {
            arguments.put(index, inputArguments.get(index));
        }

        List<ArgumentParameterContract> argumentParameters = route.getArguments();
        List<ArgumentParameterContract> boundArgumentParameters = new ArrayList<>();

        for (int key = 0; key < argumentParameters.size(); key++) {
            ArgumentParameterContract argumentParameter = argumentParameters.get(key);
            List<ArgumentContract> argumentParameterArguments = new ArrayList<>();

            // Array arguments must be last, and will take up all the remaining arguments from the
            // input
            if (argumentParameter.getValueMode() == ArgumentValueMode.ARRAY) {
                argumentParameterArguments.addAll(arguments.values());

                arguments.clear();
            } else if (arguments.containsKey(key)) {
                // If not an array type then we should match each argument in order of appearance
                argumentParameterArguments.add(arguments.remove(key));
            }

            boundArgumentParameters.add(
                    argumentParameter
                            .withArguments(
                                    argumentParameterArguments.toArray(new ArgumentContract[0]))
                            .validateValues());
        }

        return route.withArguments(
                boundArgumentParameters.toArray(new ArgumentParameterContract[0]));
    }

    /** Add the options from the input to the route. */
    protected RouteContract addOptionsToRoute(InputContract input, RouteContract route) {
        List<OptionContract> options = input.getOptions();
        List<OptionParameterContract> optionParameters = route.getOptions();
        List<OptionParameterContract> boundOptionParameters = new ArrayList<>();

        for (OptionParameterContract optionParameter : optionParameters) {
            List<OptionContract> optionParameterOptions = new ArrayList<>();

            for (OptionContract option : options) {
                // Add the option only if it matches the name or one of the short names
                if (optionParameter.getName().equals(option.getName())
                        || optionParameter.getShortNames().contains(option.getName())) {
                    optionParameterOptions.add(option);
                }
            }

            boundOptionParameters.add(
                    optionParameter
                            .withOptions(optionParameterOptions.toArray(new OptionContract[0]))
                            .validateValues());
        }

        return route.withOptions(boundOptionParameters.toArray(new OptionParameterContract[0]));
    }

    /** Do various stuff after the route has been matched. */
    @SuppressWarnings("unchecked")
    protected void routeMatched(RouteContract route) {
        routeMatchedHandler.add(route.getRouteMatchedMiddleware().toArray(new Class[0]));
        routeDispatchedHandler.add(route.getRouteDispatchedMiddleware().toArray(new Class[0]));
        throwableCaughtHandler.add(route.getThrowableCaughtMiddleware().toArray(new Class[0]));
        processExitingHandler.add(route.getProcessExitingMiddleware().toArray(new Class[0]));

        // Set the found command in the service container
        container.setSingleton(RouteContract.class, route);
    }
}
