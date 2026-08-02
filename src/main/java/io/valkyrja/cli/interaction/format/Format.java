/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.format;

import io.valkyrja.cli.interaction.format.contract.FormatContract;

public class Format implements FormatContract {

    protected String setCode;
    protected String unsetCode;

    public Format(String setCode, String unsetCode) {
        this.setCode = setCode;
        this.unsetCode = unsetCode;
    }

    @Override
    public String getSetCode() {
        return setCode;
    }

    @Override
    public Format withSetCode(String setCode) {
        Format copy = copy();
        copy.setCode = setCode;
        return copy;
    }

    @Override
    public String getUnsetCode() {
        return unsetCode;
    }

    @Override
    public Format withUnsetCode(String unsetCode) {
        Format copy = copy();
        copy.unsetCode = unsetCode;
        return copy;
    }

    protected Format copy() {
        return new Format(setCode, unsetCode);
    }
}
