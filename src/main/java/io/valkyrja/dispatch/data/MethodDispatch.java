/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data;

import io.valkyrja.dispatch.data.contract.MethodDispatchContract;
import io.valkyrja.throwable.exception.InvalidArgumentException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Dispatch targeting a class method invocation. */
public class MethodDispatch extends ClassDispatch implements MethodDispatchContract {

    protected String method;
    protected boolean isStatic;

    public MethodDispatch(Class<?> className, String method) {
        this(className, method, false, Map.of(), List.of());
    }

    public MethodDispatch(Class<?> className, String method, boolean isStatic) {
        this(className, method, isStatic, Map.of(), List.of());
    }

    public MethodDispatch(
            Class<?> className,
            String method,
            boolean isStatic,
            Map<String, Object> arguments,
            List<Class<?>> dependencies) {
        super(className, arguments, dependencies);
        this.method = method;
        this.isStatic = isStatic;
    }

    /**
     * Create a static method dispatch from a class and method name.
     *
     * @param className the target class
     * @param methodName the method name
     * @return a new static MethodDispatch
     */
    public static MethodDispatch fromArray(Class<?> className, String methodName) {
        if (methodName == null || methodName.isBlank()) {
            throw new InvalidArgumentException("methodName must not be blank");
        }
        return new MethodDispatch(className, methodName, true);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public MethodDispatchContract withMethod(String method) {
        MethodDispatch copy = copy();
        copy.method = method;
        return copy;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public MethodDispatchContract withIsStatic(boolean isStatic) {
        MethodDispatch copy = copy();
        copy.isStatic = isStatic;
        return copy;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(super.toMap());
        map.put("method", method);
        map.put("isStatic", isStatic);
        return map;
    }

    @Override
    public String toString() {
        return className.getName() + (isStatic ? "::" : "->") + method + "()";
    }

    @Override
    protected MethodDispatch copy() {
        return new MethodDispatch(className, method, isStatic, arguments, dependencies);
    }
}
