/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data;

import io.valkyrja.dispatch.data.contract.PropertyDispatchContract;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Dispatch targeting a class field (property) access. */
public class PropertyDispatch extends ClassDispatch implements PropertyDispatchContract {

    protected String property;
    protected boolean isStatic;

    public PropertyDispatch(Class<?> className, String property) {
        this(className, property, false, Map.of(), List.of());
    }

    public PropertyDispatch(Class<?> className, String property, boolean isStatic) {
        this(className, property, isStatic, Map.of(), List.of());
    }

    public PropertyDispatch(
            Class<?> className,
            String property,
            boolean isStatic,
            Map<String, Object> arguments,
            List<Class<?>> dependencies) {
        super(className, arguments, dependencies);
        this.property = property;
        this.isStatic = isStatic;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public PropertyDispatchContract withProperty(String property) {
        PropertyDispatch copy = copy();
        copy.property = property;
        return copy;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public PropertyDispatchContract withIsStatic(boolean isStatic) {
        PropertyDispatch copy = copy();
        copy.isStatic = isStatic;
        return copy;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(super.toMap());
        map.put("property", property);
        map.put("isStatic", isStatic);
        return map;
    }

    @Override
    public String toString() {
        return className.getName() + (isStatic ? "::" : "->") + property;
    }

    @Override
    protected PropertyDispatch copy() {
        return new PropertyDispatch(className, property, isStatic, arguments, dependencies);
    }
}
