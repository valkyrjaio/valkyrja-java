/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.data.abstract_;

import io.valkyrja.cli.routing.data.contract.ParameterContract;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoCastException;
import io.valkyrja.type.data.Cast;
import org.jspecify.annotations.Nullable;

public abstract class Parameter<T extends Parameter<T>> implements ParameterContract {

    protected String name;
    protected String description;
    protected @Nullable Cast cast;

    public Parameter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ParameterContract withName(String name) {
        T copy = copy();
        copy.name = name;
        return copy;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ParameterContract withDescription(String description) {
        T copy = copy();
        copy.description = description;
        return copy;
    }

    @Override
    public boolean hasCast() {
        return cast != null;
    }

    @Override
    public Cast getCast() {
        if (cast == null) {
            throw new CliRoutingNoCastException("No cast exists");
        }
        return cast;
    }

    /** Build a copy of this parameter that holds every field the subclass declares. */
    protected abstract T createCopy();

    /**
     * Build a copy that also holds the cast.
     *
     * <p>Every wither reads this method, so a subclass cannot lose the cast by forgetting to carry
     * it in {@link #createCopy()}.
     */
    protected final T copy() {
        T copy = createCopy();
        copy.cast = cast;
        return copy;
    }
}
