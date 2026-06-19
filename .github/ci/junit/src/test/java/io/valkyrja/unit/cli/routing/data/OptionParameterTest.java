/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionWithValueException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingOptionValuesValidationException;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionParameter}. */
final class OptionParameterTest {

    private static OptionParameter param() {
        return new OptionParameter("verbose", "Verbose output");
    }

    @Test
    void defaults() {
        var param = param();

        assertEquals("verbose", param.getName());
        assertSame(OptionMode.OPTIONAL, param.getMode());
        assertSame(OptionValueMode.DEFAULT, param.getValueMode());
        assertFalse(param.hasValueDisplayName());
        assertFalse(param.hasDefaultValue());
        assertTrue(param.getShortNames().isEmpty());
        assertTrue(param.getValidValues().isEmpty());
        assertTrue(param.getOptions().isEmpty());
    }

    @Test
    void shortNameMutations() {
        var param = param().withShortNames("v").withAddedShortNames("v", "V");

        assertEquals(java.util.List.of("v", "V"), param.getShortNames());
    }

    @Test
    void validValuesAndDefaultAndDisplayName() {
        var param =
                param().withValidValues("a")
                        .withAddedValidValues("a", "b")
                        .withDefaultValue("a")
                        .withValueDisplayName("LEVEL");

        assertEquals(java.util.List.of("a", "b"), param.getValidValues());
        assertTrue(param.hasDefaultValue());
        assertEquals("a", param.getDefaultValue());
        assertTrue(param.hasValueDisplayName());
        assertEquals("LEVEL", param.getValueDisplayName());
    }

    @Test
    void modeMutations() {
        var param = param();

        assertSame(OptionMode.REQUIRED, param.withMode(OptionMode.REQUIRED).getMode());
        assertSame(
                OptionValueMode.NONE, param.withValueMode(OptionValueMode.NONE).getValueMode());
    }

    @Test
    void optionMutationsAndValues() {
        var param =
                param().withOptions(new Option("verbose", "1", OptionType.LONG))
                        .withAddedOptions(new Option("verbose", "2", OptionType.LONG));

        assertEquals(2, param.getOptions().size());
        assertEquals(java.util.List.of("1", "2"), param.getCastValues());
        assertTrue(param.hasFirstValue());
        assertEquals("1", param.getFirstValue());
    }

    @Test
    void firstValueEmptyWhenNoOptions() {
        assertFalse(param().hasFirstValue());
        assertEquals("", param().getFirstValue());
    }

    @Test
    void valueModeNoneRejectsOptionsWithValue() {
        var param = (OptionParameter) param().withValueMode(OptionValueMode.NONE);
        var optionWithValue = new Option("verbose", "1", OptionType.LONG);

        assertThrows(
                CliRoutingInvalidOptionWithValueException.class,
                () -> param.withOptions(optionWithValue));
        assertThrows(
                CliRoutingInvalidOptionWithValueException.class,
                () -> param.withAddedOptions(optionWithValue));
    }

    @Test
    void areValuesValidAndValidate() {
        assertTrue(param().areValuesValid());

        var valid = param();
        assertSame(valid, valid.validateValues());

        var requiredEmpty = (OptionParameter) param().withMode(OptionMode.REQUIRED);
        assertFalse(requiredEmpty.areValuesValid());
        assertThrows(
                CliRoutingOptionValuesValidationException.class, requiredEmpty::validateValues);

        var defaultTooMany =
                (OptionParameter)
                        param().withOptions(
                                        new Option("verbose", "1", OptionType.LONG),
                                        new Option("verbose", "2", OptionType.LONG));
        assertFalse(defaultTooMany.areValuesValid());
    }
}
