/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.dispatcher;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.dispatch.data.CallableDispatch;
import io.valkyrja.dispatch.data.ClassDispatch;
import io.valkyrja.dispatch.data.ConstantDispatch;
import io.valkyrja.dispatch.data.MethodDispatch;
import io.valkyrja.dispatch.data.PropertyDispatch;
import io.valkyrja.dispatch.data.contract.ClassDispatchContract;
import io.valkyrja.dispatch.data.contract.DispatchContract;
import io.valkyrja.dispatch.dispatcher.contract.DispatcherContract;
import io.valkyrja.dispatch.throwable.exception.DispatchInvalidDispatchCapabilityException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Default dispatcher implementation.
 *
 * <p>Dispatches to class methods, fields, constants, class instantiations, and callables using Java
 * reflection, with dependency resolution via the container.
 */
public class Dispatcher implements DispatcherContract {

    protected final ContainerContract container;

    public Dispatcher() {
        this(new Container());
    }

    public Dispatcher(ContainerContract container) {
        this.container = container;
    }

    @Override
    public @Nullable Object dispatch(DispatchContract dispatch) {
        return dispatch(dispatch, Map.of());
    }

    @Override
    public @Nullable Object dispatch(DispatchContract dispatch, Map<String, Object> arguments) {
        return switch (dispatch) {
            case MethodDispatch d -> dispatchClassMethod(d, arguments);
            case PropertyDispatch d -> dispatchClassProperty(d);
            case ConstantDispatch d -> dispatchConstant(d);
            case ClassDispatch d -> dispatchClass(d, arguments);
            case CallableDispatch d -> dispatchCallable(d, arguments);
            default ->
                    throw new DispatchInvalidDispatchCapabilityException(
                            "Unknown dispatch type: " + dispatch.getClass().getName());
        };
    }

    /** Dispatch a class method. */
    protected @Nullable Object dispatchClassMethod(MethodDispatch dispatch, Map<String, Object> arguments) {
        Class<?> clazz = dispatch.getClassName();
        String methodName = dispatch.getMethod();
        Object[] args = resolveArguments(dispatch, arguments);

        Object instance = dispatch.isStatic() ? null : container.get(clazz);

        try {
            Method method = findMethod(clazz, methodName, args.length);
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new DispatchInvalidDispatchCapabilityException(
                    "Exception in " + dispatch, cause != null ? cause : e);
        } catch (ReflectiveOperationException e) {
            throw new DispatchInvalidDispatchCapabilityException("Failed to dispatch: " + dispatch, e);
        }
    }

    /** Dispatch a class field access. */
    protected @Nullable Object dispatchClassProperty(PropertyDispatch dispatch) {
        Class<?> clazz = dispatch.getClassName();
        String fieldName = dispatch.getProperty();

        try {
            Field field = clazz.getField(fieldName);
            Object instance = dispatch.isStatic() ? null : container.get(clazz);
            return field.get(instance);
        } catch (ReflectiveOperationException e) {
            throw new DispatchInvalidDispatchCapabilityException("Failed to dispatch field: " + dispatch, e);
        }
    }

    /** Dispatch a static final field constant. */
    protected @Nullable Object dispatchConstant(ConstantDispatch dispatch) {
        String constantName = dispatch.getConstant();

        try {
            if (dispatch.hasClassName()) {
                Class<?> clazz = dispatch.getClassName();
                Field field = clazz.getField(constantName);
                return field.get(null);
            }
            // Global constant — not supported in Java; throw to surface the limitation.
            throw new DispatchInvalidDispatchCapabilityException(
                    "Global constants are not supported in Java. Use a class-scoped constant: "
                            + constantName);
        } catch (ReflectiveOperationException e) {
            throw new DispatchInvalidDispatchCapabilityException("Failed to dispatch constant: " + dispatch, e);
        }
    }

    /** Dispatch a class instantiation via the container. */
    protected Object dispatchClass(ClassDispatch dispatch, Map<String, Object> arguments) {
        Class<?> clazz = dispatch.getClassName();
        Map<String, Object> args = arguments.isEmpty() ? dispatch.getArguments() : arguments;
        return container.get(clazz, args);
    }

    /** Dispatch a callable. */
    protected @Nullable Object dispatchCallable(CallableDispatch dispatch, Map<String, Object> arguments) {
        Object[] args = resolveArguments(dispatch, arguments);
        return dispatch.getCallable().apply(args);
    }

    /**
     * Resolve the full argument list for a dispatch: dependencies first, then explicit arguments.
     */
    protected Object[] resolveArguments(
            ClassDispatchContract dispatch, Map<String, Object> arguments) {
        Map<String, Object> effective = arguments.isEmpty() ? dispatch.getArguments() : arguments;
        List<Object> resolved = new ArrayList<>(resolveDependencies(dispatch));

        for (Object value : effective.values()) {
            resolved.add(resolveArgumentValue(value));
        }

        return resolved.toArray();
    }

    protected Object[] resolveArguments(CallableDispatch dispatch, Map<String, Object> arguments) {
        Map<String, Object> effective = arguments.isEmpty() ? dispatch.getArguments() : arguments;
        List<Object> resolved = new ArrayList<>(resolveDependencies(dispatch));

        for (Object value : effective.values()) {
            resolved.add(resolveArgumentValue(value));
        }

        return resolved.toArray();
    }

    /** Resolve container dependencies declared on the dispatch. */
    protected List<Object> resolveDependencies(ClassDispatchContract dispatch) {
        List<Object> deps = new ArrayList<>();
        for (Class<?> dep : dispatch.getDependencies()) {
            deps.add(container.get(dep));
        }
        return deps;
    }

    protected List<Object> resolveDependencies(CallableDispatch dispatch) {
        List<Object> deps = new ArrayList<>();
        for (Class<?> dep : dispatch.getDependencies()) {
            deps.add(container.get(dep));
        }
        return deps;
    }

    /** Resolve a single argument value — if it is itself a dispatch, recurse. */
    protected @Nullable Object resolveArgumentValue(Object argument) {
        if (argument instanceof DispatchContract nested) {
            return dispatch(nested);
        }
        return argument;
    }

    /** Find the first public method on {@code clazz} with the given name and parameter count. */
    protected Method findMethod(Class<?> clazz, String methodName, int paramCount) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == paramCount) {
                return m;
            }
        }
        throw new DispatchInvalidDispatchCapabilityException(
                "No method '"
                        + methodName
                        + "' with "
                        + paramCount
                        + " parameter(s) found on "
                        + clazz.getName());
    }
}
