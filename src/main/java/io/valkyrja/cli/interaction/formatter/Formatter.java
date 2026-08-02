/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.formatter;

import io.valkyrja.cli.interaction.format.contract.FormatContract;
import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Formatter implements FormatterContract {

    protected List<FormatContract> formats = new ArrayList<>();

    public Formatter(FormatContract... formats) {
        this.formats = new ArrayList<>(Arrays.asList(formats));
    }

    @Override
    public List<FormatContract> getFormats() {
        return Collections.unmodifiableList(formats);
    }

    @Override
    public Formatter withFormats(FormatContract... formats) {
        Formatter copy = copy();
        copy.formats = new ArrayList<>(Arrays.asList(formats));
        return copy;
    }

    @Override
    public String formatText(String text) {
        if (formats.isEmpty()) {
            return text;
        }

        String set =
                formats.stream().map(FormatContract::getSetCode).collect(Collectors.joining(";"));
        String unset =
                formats.stream().map(FormatContract::getUnsetCode).collect(Collectors.joining(";"));

        return "\033[" + set + "m" + text + "\033[" + unset + "m";
    }

    protected Formatter copy() {
        return new Formatter(formats.toArray(new FormatContract[0]));
    }
}
