/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.format.contract;

public interface FormatContract {

    String getSetCode();

    FormatContract withSetCode(String setCode);

    String getUnsetCode();

    FormatContract withUnsetCode(String unsetCode);
}
