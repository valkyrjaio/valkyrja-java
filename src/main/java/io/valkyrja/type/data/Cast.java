/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.type.data;

public class Cast {

    private final String type;
    private final boolean convert;
    private final boolean isArray;

    public Cast(String type, boolean convert, boolean isArray) {
        this.type = type;
        this.convert = convert;
        this.isArray = isArray;
    }

    public Cast(String type) {
        this(type, true, false);
    }

    public String getType() {
        return type;
    }

    public boolean isConvert() {
        return convert;
    }

    public boolean isArray() {
        return isArray;
    }
}
