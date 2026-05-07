/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.option;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.contract.OptionContract;

public class Option implements OptionContract {

    protected String name;
    protected String value;
    protected OptionType type;

    public Option(String name, String value, OptionType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public Option(String name, OptionType type) {
        this(name, "", type);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OptionContract withName(String name) {
        Option copy = new Option(this.name, this.value, this.type);
        copy.name = name;
        return copy;
    }

    @Override
    public boolean hasValue() {
        return !value.isEmpty();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public OptionContract withValue(String value) {
        Option copy = new Option(this.name, this.value, this.type);
        copy.value = value;
        return copy;
    }

    @Override
    public OptionContract withoutValue() {
        Option copy = new Option(this.name, this.value, this.type);
        copy.value = "";
        return copy;
    }

    @Override
    public OptionType getType() {
        return type;
    }

    @Override
    public OptionContract withType(OptionType type) {
        Option copy = new Option(this.name, this.value, this.type);
        copy.type = type;
        return copy;
    }
}