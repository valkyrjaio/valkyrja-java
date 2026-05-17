/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.param.abstract_;

import io.valkyrja.http.message.param.contract.ParamCollectionContract;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public abstract class ParamCollection implements ParamCollectionContract {

    protected Map<String, Object> params = new LinkedHashMap<>();

    public ParamCollection(Map<String, Object> params) {
        validateParams(params);
        this.params = new LinkedHashMap<>(params);
    }

    public static ParamCollection fromArray(Map<String, Object> data) {
        throw new UnsupportedOperationException("fromArray must be called on a concrete subclass");
    }

    protected static ParamCollection fromArrayInternal(
            Map<String, Object> data,
            java.util.function.Function<Map<String, Object>, ParamCollection> constructor) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            validateParam(value);
            result.put(entry.getKey(), value);
        }

        return constructor.apply(result);
    }

    protected static void validateParam(Object param) {
        if (!isValidParam(param)) {
            throw new IllegalArgumentException("Param must be scalar or null");
        }
    }

    protected static boolean isValidParam(Object param) {
        return param == null
                || param instanceof String
                || param instanceof Integer
                || param instanceof Long
                || param instanceof Double
                || param instanceof Float
                || param instanceof Boolean;
    }

    @Override
    public boolean has(String key) {
        return params.containsKey(key);
    }

    @Override
    public @Nullable Object get(String key) {
        return params.getOrDefault(key, null);
    }

    @Override
    public Map<String, Object> getAll() {
        return new LinkedHashMap<>(params);
    }

    @Override
    public Map<String, Object> getOnly(String... keys) {
        Set<String> keySet = Arrays.stream(keys).collect(Collectors.toSet());
        return params.entrySet().stream()
                .filter(e -> keySet.contains(e.getKey()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                LinkedHashMap::new));
    }

    @Override
    public Map<String, Object> getAllExcept(String... keys) {
        Set<String> keySet = Arrays.stream(keys).collect(Collectors.toSet());
        return params.entrySet().stream()
                .filter(e -> !keySet.contains(e.getKey()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                LinkedHashMap::new));
    }

    @Override
    public ParamCollectionContract with(Map<String, Object> params) {
        validateParams(params);
        ParamCollection copy = copy();
        copy.params = new LinkedHashMap<>(params);
        return copy;
    }

    @Override
    public ParamCollectionContract withAdded(Map<String, Object> params) {
        validateParams(params);
        ParamCollection copy = copy();
        copy.params = new LinkedHashMap<>(this.params);
        copy.params.putAll(params);
        return copy;
    }

    protected void validateParams(Map<String, Object> params) {
        for (Object param : params.values()) {
            validateParam(param);
        }
    }

    protected abstract ParamCollection copy();
}
