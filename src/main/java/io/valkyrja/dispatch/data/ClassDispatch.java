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
import io.valkyrja.dispatch.data.contract.ClassDispatchContract;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Dispatch targeting a class instantiation via the container. */
public class ClassDispatch extends Dispatch implements ClassDispatchContract {

    protected Class<?> className;
    protected Map<String, Object> arguments;
    protected List<Class<?>> dependencies;

    public ClassDispatch(Class<?> className) {
        this(className, Map.of(), List.of());
    }

    public ClassDispatch(
            Class<?> className, Map<String, Object> arguments, List<Class<?>> dependencies) {
        this.className = className;
        this.arguments = Map.copyOf(arguments);
        this.dependencies = List.copyOf(dependencies);
    }

    @Override
    public Class<?> getClassName() {
        return className;
    }

    @Override
    public ClassDispatchContract withClassName(Class<?> className) {
        return new ClassDispatch(className, arguments, dependencies);
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public ClassDispatchContract withArguments(Map<String, Object> arguments) {
        return new ClassDispatch(className, arguments, dependencies);
    }

    @Override
    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public ClassDispatchContract withDependencies(List<Class<?>> dependencies) {
        return new ClassDispatch(className, arguments, dependencies);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("class", className.getName());
        map.put("arguments", arguments);
        map.put("dependencies", dependencies.stream().map(Class::getName).toList());
        return map;
    }

    @Override
    public String toString() {
        return className.getName();
    }

    protected ClassDispatch copy() {
        return new ClassDispatch(className, arguments, dependencies);
    }
}
