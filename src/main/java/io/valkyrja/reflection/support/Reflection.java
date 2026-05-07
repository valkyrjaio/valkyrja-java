/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.reflection.support;

import io.valkyrja.reflection.throwable.exception.ReflectionInvalidClassToInstantiateException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Reflection {

    protected static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
    protected static final Map<Class<?>, Constructor<?>> constructors = new ConcurrentHashMap<>();
    protected static final Map<String, Method> methods = new ConcurrentHashMap<>();

    private Reflection() {}

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(Class<T> className) {
        return (T) instances.computeIfAbsent(className, Reflection::resolve);
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> constructor(Class<T> className) {
        return (Constructor<T>)
                constructors.computeIfAbsent(className, Reflection::resolveConstructor);
    }

    public static Method method(Class<?> cls, String name, Class<?>... paramTypes) {
        String key =
                cls.getName()
                        + "#"
                        + name
                        + "("
                        + Arrays.stream(paramTypes)
                                .map(Class::getName)
                                .collect(Collectors.joining(","))
                        + ")";
        return methods.computeIfAbsent(key, k -> resolveMethod(cls, name, paramTypes));
    }

    protected static <T> T resolve(Class<T> className) {
        try {
            return Reflection.constructor(className).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ReflectionInvalidClassToInstantiateException(
                    "Failed to instantiate " + className.getName());
        }
    }

    protected static <T> Constructor<T> resolveConstructor(Class<T> className) {
        try {
            return className.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ReflectionInvalidClassToInstantiateException(
                    "Failed to get constructor for " + className.getName());
        }
    }

    protected static Method resolveMethod(Class<?> cls, String name, Class<?>... paramTypes) {
        try {
            return cls.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new ReflectionInvalidClassToInstantiateException(
                    "Failed to get method " + name + " for " + cls.getName());
        }
    }
}
