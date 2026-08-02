/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data;

import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ProcessExitingMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidArgumentNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoHelpTextException;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Route implements RouteContract {

    protected String name;
    protected String description;
    protected @Nullable Supplier<MessageContract> helpText;
    protected BiFunction<ContainerContract, RouteContract, OutputContract> handler;
    protected List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware;
    protected List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware;
    protected List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware;
    protected List<Class<? extends ProcessExitingMiddlewareContract>> processExitingMiddleware;
    protected List<ArgumentParameterContract> arguments;
    protected List<OptionParameterContract> options;

    public Route(
            String name,
            String description,
            BiFunction<ContainerContract, RouteContract, OutputContract> handler) {
        this(
                name,
                description,
                handler,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public Route(
            String name,
            String description,
            BiFunction<ContainerContract, RouteContract, OutputContract> handler,
            @Nullable Supplier<MessageContract> helpText,
            List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
            List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
            List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
            List<Class<? extends ProcessExitingMiddlewareContract>> processExitingMiddleware,
            List<ArgumentParameterContract> arguments,
            List<OptionParameterContract> options) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.helpText = helpText;
        this.routeMatchedMiddleware = new ArrayList<>(routeMatchedMiddleware);
        this.routeDispatchedMiddleware = new ArrayList<>(routeDispatchedMiddleware);
        this.throwableCaughtMiddleware = new ArrayList<>(throwableCaughtMiddleware);
        this.processExitingMiddleware = new ArrayList<>(processExitingMiddleware);
        this.arguments = new ArrayList<>(arguments);
        this.options = new ArrayList<>(options);
    }

    protected Route copy() {
        return new Route(
                name,
                description,
                handler,
                helpText,
                routeMatchedMiddleware,
                routeDispatchedMiddleware,
                throwableCaughtMiddleware,
                processExitingMiddleware,
                arguments,
                options);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RouteContract withName(String name) {
        Route copy = copy();
        copy.name = name;
        return copy;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public RouteContract withDescription(String description) {
        Route copy = copy();
        copy.description = description;
        return copy;
    }

    @Override
    public boolean hasHelpText() {
        return helpText != null;
    }

    @Override
    public Supplier<MessageContract> getHelpText() {
        if (helpText == null) {
            throw new CliRoutingNoHelpTextException("No help text has been set for this route");
        }
        return helpText;
    }

    @Override
    public MessageContract getHelpTextMessage() {
        return getHelpText().get();
    }

    @Override
    public RouteContract withHelpText(Supplier<MessageContract> helpText) {
        Route copy = copy();
        copy.helpText = helpText;
        return copy;
    }

    @Override
    public boolean hasArguments() {
        return !arguments.isEmpty();
    }

    @Override
    public List<ArgumentParameterContract> getArguments() {
        return arguments;
    }

    @Override
    public boolean hasArgument(String name) {
        return arguments.stream().anyMatch(a -> a.getName().equals(name));
    }

    @Override
    public ArgumentParameterContract getArgument(String name) {
        return arguments.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () ->
                                new CliRoutingInvalidArgumentNameException(
                                        "The argument `" + name + "` was not found"));
    }

    @Override
    public RouteContract withArguments(ArgumentParameterContract... arguments) {
        Route copy = copy();
        copy.arguments = new ArrayList<>(Arrays.asList(arguments));
        return copy;
    }

    @Override
    public RouteContract withAddedArguments(ArgumentParameterContract... arguments) {
        Route copy = copy();
        copy.arguments = new ArrayList<>(this.arguments);
        copy.arguments.addAll(Arrays.asList(arguments));
        return copy;
    }

    @Override
    public boolean hasOptions() {
        return !options.isEmpty();
    }

    @Override
    public List<OptionParameterContract> getOptions() {
        return options;
    }

    @Override
    public boolean hasOption(String name) {
        return options.stream().anyMatch(o -> o.getName().equals(name));
    }

    @Override
    public OptionParameterContract getOption(String name) {
        return options.stream()
                .filter(o -> o.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () ->
                                new CliRoutingInvalidOptionNameException(
                                        "The option `" + name + "` was not found"));
    }

    @Override
    public RouteContract withOptions(OptionParameterContract... options) {
        Route copy = copy();
        copy.options = new ArrayList<>(Arrays.asList(options));
        return copy;
    }

    @Override
    public RouteContract withAddedOptions(OptionParameterContract... options) {
        Route copy = copy();
        copy.options = new ArrayList<>(this.options);
        copy.options.addAll(Arrays.asList(options));
        return copy;
    }

    @Override
    public List<Class<? extends RouteMatchedMiddlewareContract>> getRouteMatchedMiddleware() {
        return routeMatchedMiddleware;
    }

    @Override
    public RouteContract withRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeMatchedMiddleware = new ArrayList<>(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedRouteMatchedMiddleware(
            List<Class<? extends RouteMatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeMatchedMiddleware = new ArrayList<>(this.routeMatchedMiddleware);
        copy.routeMatchedMiddleware.addAll(middleware);
        return copy;
    }

    @Override
    public List<Class<? extends RouteDispatchedMiddlewareContract>> getRouteDispatchedMiddleware() {
        return routeDispatchedMiddleware;
    }

    @Override
    public RouteContract withRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeDispatchedMiddleware = new ArrayList<>(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedRouteDispatchedMiddleware(
            List<Class<? extends RouteDispatchedMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.routeDispatchedMiddleware = new ArrayList<>(this.routeDispatchedMiddleware);
        copy.routeDispatchedMiddleware.addAll(middleware);
        return copy;
    }

    @Override
    public List<Class<? extends ThrowableCaughtMiddlewareContract>> getThrowableCaughtMiddleware() {
        return throwableCaughtMiddleware;
    }

    @Override
    public RouteContract withThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.throwableCaughtMiddleware = new ArrayList<>(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedThrowableCaughtMiddleware(
            List<Class<? extends ThrowableCaughtMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.throwableCaughtMiddleware = new ArrayList<>(this.throwableCaughtMiddleware);
        copy.throwableCaughtMiddleware.addAll(middleware);
        return copy;
    }

    @Override
    public List<Class<? extends ProcessExitingMiddlewareContract>> getProcessExitingMiddleware() {
        return processExitingMiddleware;
    }

    @Override
    public RouteContract withProcessExitingMiddleware(
            List<Class<? extends ProcessExitingMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.processExitingMiddleware = new ArrayList<>(middleware);
        return copy;
    }

    @Override
    public RouteContract withAddedProcessExitingMiddleware(
            List<Class<? extends ProcessExitingMiddlewareContract>> middleware) {
        Route copy = copy();
        copy.processExitingMiddleware = new ArrayList<>(this.processExitingMiddleware);
        copy.processExitingMiddleware.addAll(middleware);
        return copy;
    }

    @Override
    public BiFunction<ContainerContract, RouteContract, OutputContract> getHandler() {
        return handler;
    }

    @Override
    public RouteContract withHandler(
            BiFunction<ContainerContract, RouteContract, OutputContract> handler) {
        Route copy = copy();
        copy.handler = handler;
        return copy;
    }
}
