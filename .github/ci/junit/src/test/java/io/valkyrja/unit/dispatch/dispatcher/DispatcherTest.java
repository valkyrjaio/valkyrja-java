/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.dispatch.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.fixtures.dispatch.DispatchableClass;
import io.valkyrja.fixtures.dispatch.UnknownDispatchClass;
import io.valkyrja.container.manager.Container;
import io.valkyrja.dispatch.data.CallableDispatch;
import io.valkyrja.dispatch.data.ClassDispatch;
import io.valkyrja.dispatch.data.ConstantDispatch;
import io.valkyrja.dispatch.data.MethodDispatch;
import io.valkyrja.dispatch.data.PropertyDispatch;
import io.valkyrja.dispatch.dispatcher.Dispatcher;
import io.valkyrja.dispatch.throwable.exception.DispatchInvalidDispatchCapabilityException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the {@link Dispatcher}. */
final class DispatcherTest {

    private final Dispatcher dispatcher = new Dispatcher(new Container());

    @Test
    void noArgConstructorUsesNewContainer() {
        var result = new Dispatcher().dispatch(new MethodDispatch(DispatchableClass.class, "staticMethod", true));

        assertEquals("static-result", result);
    }

    // -- methods --

    @Test
    void dispatchesStaticMethod() {
        var result = dispatcher.dispatch(new MethodDispatch(DispatchableClass.class, "staticMethod", true));

        assertEquals("static-result", result);
    }

    @Test
    void dispatchesInstanceMethod() {
        var result = dispatcher.dispatch(new MethodDispatch(DispatchableClass.class, "instanceMethod"));

        assertEquals("instance-result", result);
    }

    @Test
    void dispatchesMethodWithArgument() {
        var dispatch =
                new MethodDispatch(
                        DispatchableClass.class, "echo", false, Map.of("value", "hi"), List.of());

        assertEquals("echo:hi", dispatcher.dispatch(dispatch));
    }

    @Test
    void dispatchesMethodWithNestedDispatchArgument() {
        var nested = new ConstantDispatch("CONSTANT", DispatchableClass.class);
        var dispatch =
                new MethodDispatch(
                        DispatchableClass.class,
                        "echo",
                        false,
                        Map.of("value", nested),
                        List.of());

        assertEquals("echo:constant-value", dispatcher.dispatch(dispatch));
    }

    @Test
    void wrapsExceptionThrownInsideMethod() {
        var dispatch = new MethodDispatch(DispatchableClass.class, "boom", true);

        var thrown =
                assertThrows(
                        DispatchInvalidDispatchCapabilityException.class,
                        () -> dispatcher.dispatch(dispatch));

        assertInstanceOf(IllegalStateException.class, thrown.getCause());
    }

    @Test
    void throwsWhenMethodNotFound() {
        var dispatch = new MethodDispatch(DispatchableClass.class, "missing", true);

        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(dispatch));
    }

    @Test
    void dispatchesMethodWithExternalArguments() {
        var dispatch = new MethodDispatch(DispatchableClass.class, "echo");

        // External arguments take precedence over the dispatch's own (empty) arguments.
        assertEquals("echo:ext", dispatcher.dispatch(dispatch, Map.of("value", "ext")));
    }

    @Test
    void dispatchesMethodWithDependencies() {
        var dispatch =
                new MethodDispatch(
                        DispatchableClass.class,
                        "echo",
                        false,
                        Map.of(),
                        List.of(DispatchableClass.class));

        // The single dependency is resolved from the container and passed as the only argument.
        assertNotNull(dispatcher.dispatch(dispatch));
    }

    @Test
    void wrapsReflectiveOperationExceptionFromInvoke() {
        // findMethod is overridden to return an inaccessible private method, so invoke() throws
        // IllegalAccessException — a ReflectiveOperationException that is not an InvocationTarget.
        var dispatcher =
                new Dispatcher(new Container()) {
                    @Override
                    protected java.lang.reflect.Method findMethod(
                            Class<?> clazz, String methodName, int paramCount) {
                        try {
                            return DispatchableClass.class.getDeclaredMethod("inaccessibleMethod");
                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                };

        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(new MethodDispatch(DispatchableClass.class, "anything")));
    }

    // -- properties --

    @Test
    void dispatchesStaticProperty() {
        var result = dispatcher.dispatch(new PropertyDispatch(DispatchableClass.class, "staticField", true));

        assertEquals("static-field", result);
    }

    @Test
    void dispatchesInstanceProperty() {
        var result = dispatcher.dispatch(new PropertyDispatch(DispatchableClass.class, "instanceField"));

        assertEquals("instance-field", result);
    }

    @Test
    void throwsWhenPropertyNotFound() {
        var dispatch = new PropertyDispatch(DispatchableClass.class, "missing", true);

        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(dispatch));
    }

    // -- constants --

    @Test
    void dispatchesConstant() {
        var result = dispatcher.dispatch(new ConstantDispatch("CONSTANT", DispatchableClass.class));

        assertEquals("constant-value", result);
    }

    @Test
    void throwsForGlobalConstantWithoutClass() {
        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(new ConstantDispatch("CONSTANT")));
    }

    @Test
    void throwsWhenConstantNotFound() {
        var dispatch = new ConstantDispatch("MISSING", DispatchableClass.class);

        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(dispatch));
    }

    // -- class instantiation --

    @Test
    void dispatchesClassInstantiation() {
        var result = dispatcher.dispatch(new ClassDispatch(DispatchableClass.class));

        assertInstanceOf(DispatchableClass.class, result);
    }

    @Test
    void dispatchesClassInstantiationWithDispatchArguments() {
        var dispatch = new ClassDispatch(DispatchableClass.class, Map.of(), List.of());

        assertNotNull(dispatcher.dispatch(dispatch, Map.of()));
    }

    @Test
    void dispatchesClassInstantiationWithExternalArguments() {
        var dispatch = new ClassDispatch(DispatchableClass.class);

        // Non-empty external arguments are forwarded to the container.
        assertInstanceOf(
                DispatchableClass.class, dispatcher.dispatch(dispatch, Map.of("a", 1)));
    }

    // -- callables --

    @Test
    void dispatchesCallable() {
        Function<Object[], Object> callable = args -> "called:" + args.length;

        assertEquals("called:0", dispatcher.dispatch(new CallableDispatch(callable)));
    }

    @Test
    void dispatchesCallableWithArgumentsAndDependencies() {
        Function<Object[], Object> callable = args -> "args:" + args.length;
        var dispatch =
                new CallableDispatch(
                        callable, Map.of("value", "x"), List.of(DispatchableClass.class));

        // one dependency + one argument = 2 resolved args
        assertEquals("args:2", dispatcher.dispatch(dispatch));
    }

    // -- unknown --

    @Test
    void throwsForUnknownDispatchType() {
        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(new UnknownDispatchClass()));
    }

    @Test
    void dispatchesCallableWithExternalArguments() {
        Function<Object[], Object> callable = args -> "args:" + args.length;
        var dispatch = new CallableDispatch(callable);

        // Non-empty external arguments take precedence over the dispatch's own.
        assertEquals("args:1", dispatcher.dispatch(dispatch, Map.of("value", "ext")));
    }

    @Test
    void findMethodSkipsNameMatchWithWrongArgumentCount() {
        // "echo" exists but takes one parameter; dispatching with zero args matches the name but
        // not the parameter count, so no method is found.
        var dispatch = new MethodDispatch(DispatchableClass.class, "echo", true);

        assertThrows(
                DispatchInvalidDispatchCapabilityException.class,
                () -> dispatcher.dispatch(dispatch));
    }

}
