/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.caster;

import io.valkyrja.cli.routing.caster.contract.CasterContract;
import io.valkyrja.cli.routing.constant.CastArgument;
import io.valkyrja.cli.routing.data.contract.ParameterContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.type.contract.TypeContract;
import io.valkyrja.type.data.Cast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class Caster implements CasterContract {

    protected final ContainerContract container;

    public Caster(ContainerContract container) {
        this.container = container;
    }

    @Override
    public List<@Nullable Object> getCastValues(ParameterContract parameter) {
        List<String> values = parameter.getValues();

        if (!parameter.hasCast()) {
            return new ArrayList<>(values);
        }

        Cast cast = parameter.getCast();
        List<@Nullable Object> castValues = new ArrayList<>(values.size());

        for (String value : values) {
            TypeContract type =
                    container.getService(cast.getType(), Map.of(CastArgument.VALUE, value));

            castValues.add(cast.isConvert() ? type.asValue() : type);
        }

        return castValues;
    }
}
