/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
