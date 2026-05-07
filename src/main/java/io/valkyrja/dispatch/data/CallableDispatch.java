/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data;

import io.valkyrja.dispatch.data.abstract_.Dispatch;
import io.valkyrja.dispatch.data.contract.CallableDispatchContract;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Dispatch targeting a callable (lambda/function reference). */
public class CallableDispatch extends Dispatch implements CallableDispatchContract {

    protected Function<Object[], Object> callable;
    protected Map<String, Object> arguments;
    protected List<Class<?>> dependencies;

    public CallableDispatch(Function<Object[], Object> callable) {
        this(callable, Map.of(), List.of());
    }

    public CallableDispatch(
            Function<Object[], Object> callable,
            Map<String, Object> arguments,
            List<Class<?>> dependencies) {
        this.callable = callable;
        this.arguments = Map.copyOf(arguments);
        this.dependencies = List.copyOf(dependencies);
    }

    @Override
    public Function<Object[], Object> getCallable() {
        return callable;
    }

    @Override
    public CallableDispatchContract withCallable(Function<Object[], Object> callable) {
        return new CallableDispatch(callable, arguments, dependencies);
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public CallableDispatchContract withArguments(Map<String, Object> arguments) {
        return new CallableDispatch(callable, arguments, dependencies);
    }

    @Override
    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public CallableDispatchContract withDependencies(List<Class<?>> dependencies) {
        return new CallableDispatch(callable, arguments, dependencies);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("callable", callable.toString());
        map.put("arguments", arguments);
        map.put("dependencies", dependencies.stream().map(Class::getName).toList());
        return map;
    }

    @Override
    public String toString() {
        return callable.toString();
    }
}
