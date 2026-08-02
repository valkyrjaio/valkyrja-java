/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.formatter.contract;

import io.valkyrja.cli.interaction.format.contract.FormatContract;
import java.util.List;

public interface FormatterContract {

    List<FormatContract> getFormats();

    FormatterContract withFormats(FormatContract... formats);

    String formatText(String text);
}
