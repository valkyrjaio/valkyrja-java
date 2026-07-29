/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.data;

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
        assertSame(OptionValueMode.NONE, param.withValueMode(OptionValueMode.NONE).getValueMode());
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

    @Test
    void valueModeNoneRejectsOptionsWithValueOnAdders() {
        var param = param().withValueMode(OptionValueMode.NONE);

        assertThrows(
                CliRoutingInvalidOptionWithValueException.class,
                () -> param.withOptions(new Option("verbose", "1", OptionType.LONG)));
        assertThrows(
                CliRoutingInvalidOptionWithValueException.class,
                () -> param.withAddedOptions(new Option("verbose", "1", OptionType.LONG)));
    }

    @Test
    void valueModeNoneAllowsValuelessOptions() {
        var param = param().withValueMode(OptionValueMode.NONE);

        assertEquals(
                1, param.withOptions(new Option("verbose", OptionType.LONG)).getOptions().size());
        assertEquals(
                1,
                param.withAddedOptions(new Option("verbose", OptionType.LONG)).getOptions().size());
    }

    @Test
    void areValuesValidCoversModeAndValueModeBranches() {
        assertFalse(param().withMode(OptionMode.REQUIRED).areValuesValid());
        assertTrue(
                param().withMode(OptionMode.REQUIRED)
                        .withOptions(new Option("verbose", "1", OptionType.LONG))
                        .areValuesValid());
        assertTrue(param().areValuesValid());
        assertTrue(param().withValueMode(OptionValueMode.NONE).areValuesValid());
    }

    @Test
    void areValuesValidEnforcesValidValues() {
        var validValue = new Option("verbose", "a", OptionType.LONG);
        var validValue2 = new Option("verbose", "b", OptionType.LONG);
        var invalidValue = new Option("verbose", "c", OptionType.LONG);

        var param = param().withValidValues("a", "b");

        // Empty valid values impose no constraint on the bound value
        assertTrue(param().withOptions(invalidValue).areValuesValid());
        // A provided value that is a member of the valid values passes
        assertTrue(param.withOptions(validValue).areValuesValid());
        // A provided value that is not a member of the valid values fails
        assertFalse(param.withOptions(invalidValue).areValuesValid());
        // ARRAY: every provided value must be a member of the valid values
        assertTrue(
                param.withValueMode(OptionValueMode.ARRAY)
                        .withOptions(validValue, validValue2)
                        .areValuesValid());
        // ARRAY: a single invalid value among several fails
        assertFalse(
                param.withValueMode(OptionValueMode.ARRAY)
                        .withOptions(validValue, validValue2, invalidValue)
                        .areValuesValid());
        // Non-empty valid values with no bound options impose no failure
        assertTrue(param.areValuesValid());
        // Interaction with REQUIRED: a required, member value passes
        assertTrue(param.withMode(OptionMode.REQUIRED).withOptions(validValue).areValuesValid());
        // Interaction with REQUIRED: a required, non-member value fails
        assertFalse(param.withMode(OptionMode.REQUIRED).withOptions(invalidValue).areValuesValid());
    }

    @Test
    void validateValuesThrowsOnInvalidValue() {
        var invalid =
                param().withValidValues("a", "b")
                        .withOptions(new Option("verbose", "c", OptionType.LONG));

        assertThrows(CliRoutingOptionValuesValidationException.class, invalid::validateValues);
    }

    @Test
    void validateValuesPassesWithValidValue() {
        var valid =
                param().withValidValues("a", "b")
                        .withOptions(new Option("verbose", "a", OptionType.LONG));

        assertSame(valid, valid.validateValues());
    }
}
