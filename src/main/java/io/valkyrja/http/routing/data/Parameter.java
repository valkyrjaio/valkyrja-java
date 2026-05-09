/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data;

import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoCastException;
import io.valkyrja.type.data.Cast;

public class Parameter implements ParameterContract {

    protected String name;
    protected String regex;
    protected Cast cast;
    protected boolean isOptional;
    protected boolean shouldCapture;
    protected Object defaultValue;
    protected Object value;

    public Parameter(String name, String regex) {
        this(name, regex, null, false, true, null, null);
    }

    public Parameter(String name, String regex, Cast cast, boolean isOptional, boolean shouldCapture, Object defaultValue, Object value) {
        this.name = name;
        this.regex = regex;
        this.cast = cast;
        this.isOptional = isOptional;
        this.shouldCapture = shouldCapture;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ParameterContract withName(String name) {
        Parameter copy = copy();
        copy.name = name;
        return copy;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public ParameterContract withRegex(String regex) {
        Parameter copy = copy();
        copy.regex = regex;
        return copy;
    }

    @Override
    public boolean hasCast() {
        return cast != null;
    }

    @Override
    public Cast getCast() {
        if (cast == null) {
            throw new HttpRoutingNoCastException("No cast exists");
        }
        return cast;
    }

    @Override
    public ParameterContract withCast(Cast cast) {
        Parameter copy = copy();
        copy.cast = cast;
        return copy;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public ParameterContract withIsOptional(boolean isOptional) {
        Parameter copy = copy();
        copy.isOptional = isOptional;
        return copy;
    }

    @Override
    public boolean shouldCapture() {
        return shouldCapture;
    }

    @Override
    public ParameterContract withShouldCapture(boolean shouldCapture) {
        Parameter copy = copy();
        copy.shouldCapture = shouldCapture;
        return copy;
    }

    @Override
    public Object getDefault() {
        return defaultValue;
    }

    @Override
    public ParameterContract withDefault(Object defaultValue) {
        Parameter copy = copy();
        copy.defaultValue = defaultValue;
        return copy;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public ParameterContract withValue(Object value) {
        Parameter copy = copy();
        copy.value = value;
        return copy;
    }

    protected Parameter copy() {
        return new Parameter(name, regex, cast, isOptional, shouldCapture, defaultValue, value);
    }
}