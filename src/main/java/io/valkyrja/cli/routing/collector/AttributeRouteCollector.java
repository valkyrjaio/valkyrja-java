/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.collector;

import io.valkyrja.cli.middleware.contract.ExitedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.routing.attribute.ArgumentParameter;
import io.valkyrja.cli.routing.attribute.OptionParameter;
import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.attribute.route.Middleware;
import io.valkyrja.cli.routing.attribute.route.Name;
import io.valkyrja.cli.routing.attribute.route.RouteHandler;
import io.valkyrja.cli.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class AttributeRouteCollector implements RouteCollectorContract {

    @Override
    public List<RouteContract> getRoutes(Class<?>... classes) {
        List<RouteContract> routes = new ArrayList<>();

        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                Route[] routeAnnotations = method.getAnnotationsByType(Route.class);
                for (Route routeAnnotation : routeAnnotations) {
                    io.valkyrja.cli.routing.data.Route route = convertAnnotationToData(routeAnnotation, clazz, method);
                    route = updateName(route, clazz, method);
                    route = updateMiddleware(route, method);
                    route = updateArguments(route, method);
                    route = updateOptions(route, method);
                    routes.add(route);
                }
            }
        }

        return routes;
    }

    protected io.valkyrja.cli.routing.data.Route convertAnnotationToData(Route annotation, Class<?> clazz, Method method) {
        RouteHandler handlerAnnotation = method.getAnnotation(RouteHandler.class);
        BiFunction<ContainerContract, RouteContract, OutputContract> handler;

        if (handlerAnnotation != null) {
            Class<?> handlerClass = handlerAnnotation.handlerClass();
            String handlerMethod = handlerAnnotation.handlerMethod();
            handler = (container, route) -> {
                try {
                    Object instance = handlerClass.getDeclaredConstructor().newInstance();
                    Method m = handlerClass.getMethod(handlerMethod, ContainerContract.class, RouteContract.class);
                    return (OutputContract) m.invoke(instance, container, route);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            handler = (container, route) -> new io.valkyrja.cli.interaction.output.Output();
        }

        return new io.valkyrja.cli.routing.data.Route(
            annotation.name(),
            annotation.description(),
            handler
        );
    }

    protected io.valkyrja.cli.routing.data.Route updateName(io.valkyrja.cli.routing.data.Route route, Class<?> clazz, Method method) {
        Name classNameAnnotation = clazz.getAnnotation(Name.class);
        Name methodNameAnnotation = method.getAnnotation(Name.class);

        if (classNameAnnotation != null) {
            route = (io.valkyrja.cli.routing.data.Route) route.withName(classNameAnnotation.value() + "." + route.getName());
        }
        if (methodNameAnnotation != null) {
            route = (io.valkyrja.cli.routing.data.Route) route.withName(route.getName() + "." + methodNameAnnotation.value());
        }

        return route;
    }

    @SuppressWarnings("unchecked")
    protected io.valkyrja.cli.routing.data.Route updateMiddleware(io.valkyrja.cli.routing.data.Route route, Method method) {
        Middleware[] middlewares = method.getAnnotationsByType(Middleware.class);

        for (Middleware m : middlewares) {
            Class<?> middlewareClass = m.name();

            if (RouteMatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                List<Class<? extends RouteMatchedMiddlewareContract>> list = new ArrayList<>();
                list.add((Class<? extends RouteMatchedMiddlewareContract>) middlewareClass);
                route = (io.valkyrja.cli.routing.data.Route) route.withAddedRouteMatchedMiddleware(list);
            }

            if (RouteDispatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                List<Class<? extends RouteDispatchedMiddlewareContract>> list = new ArrayList<>();
                list.add((Class<? extends RouteDispatchedMiddlewareContract>) middlewareClass);
                route = (io.valkyrja.cli.routing.data.Route) route.withAddedRouteDispatchedMiddleware(list);
            }

            if (ThrowableCaughtMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                List<Class<? extends ThrowableCaughtMiddlewareContract>> list = new ArrayList<>();
                list.add((Class<? extends ThrowableCaughtMiddlewareContract>) middlewareClass);
                route = (io.valkyrja.cli.routing.data.Route) route.withAddedThrowableCaughtMiddleware(list);
            }

            if (ExitedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                List<Class<? extends ExitedMiddlewareContract>> list = new ArrayList<>();
                list.add((Class<? extends ExitedMiddlewareContract>) middlewareClass);
                route = (io.valkyrja.cli.routing.data.Route) route.withAddedExitedMiddleware(list);
            }
        }

        return route;
    }

    protected io.valkyrja.cli.routing.data.Route updateArguments(io.valkyrja.cli.routing.data.Route route, Method method) {
        ArgumentParameter[] annotations = method.getAnnotationsByType(ArgumentParameter.class);
        List<ArgumentParameterContract> params = new ArrayList<>(route.getArguments());

        for (ArgumentParameter a : annotations) {
            params.add(new io.valkyrja.cli.routing.data.ArgumentParameter(
                a.name(), a.description(), a.mode(), a.valueMode(), new ArrayList<>()
            ));
        }

        return (io.valkyrja.cli.routing.data.Route) route.withArguments(params.toArray(new ArgumentParameterContract[0]));
    }

    protected io.valkyrja.cli.routing.data.Route updateOptions(io.valkyrja.cli.routing.data.Route route, Method method) {
        OptionParameter[] annotations = method.getAnnotationsByType(OptionParameter.class);
        List<OptionParameterContract> params = new ArrayList<>(route.getOptions());

        for (OptionParameter o : annotations) {
            params.add(new io.valkyrja.cli.routing.data.OptionParameter(
                o.name(), o.description(), o.valueDisplayName(), o.defaultValue(),
                Arrays.asList(o.shortNames()), Arrays.asList(o.validValues()),
                new ArrayList<>(), o.mode(), o.valueMode()
            ));
        }

        return (io.valkyrja.cli.routing.data.Route) route.withOptions(params.toArray(new OptionParameterContract[0]));
    }
}
