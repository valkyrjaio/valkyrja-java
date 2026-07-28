/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.functional.cli.interaction.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.input.factory.InputFactory;
import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidNonEmptyValueException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Message-mapping fidelity for an incoming CLI command.
 *
 * <p>Asserts that an argv-style array lands on the framework's own {@link
 * io.valkyrja.cli.interaction.input.Input Input}, {@link
 * io.valkyrja.cli.interaction.argument.Argument Argument}, and {@link
 * io.valkyrja.cli.interaction.option.Option Option} objects exactly as spelled, independent of
 * routing.
 *
 * <p>Mirrors the PHP reference {@code Tests\Functional\Cli\Interaction\Input\InputMappingTest}. One
 * axis differs by language rather than by intent: {@code main(String[] args)} carries no program
 * name, so the caller is always the supplied application name and the command name is read from
 * index 0. See the argv-conventions table in the architecture guide.
 */
final class InputMappingTest {

    private static final String DEFAULT_CALLER = "valkyrja";

    private static final String DEFAULT_COMMAND = "list";

    /** Every option spelling the factory supports. */
    static Stream<Arguments> provideOptionSpellings() {
        return Stream.of(
                Arguments.of(
                        "long with value",
                        new String[] {"cmd", "--name=value"},
                        List.of(tuple("name", "value", OptionType.LONG))),
                Arguments.of(
                        "long without value",
                        new String[] {"cmd", "--verbose"},
                        List.of(tuple("verbose", "", OptionType.LONG))),
                Arguments.of(
                        "long with empty value",
                        new String[] {"cmd", "--name="},
                        List.of(tuple("name", "", OptionType.LONG))),
                Arguments.of(
                        "short without value",
                        new String[] {"cmd", "-v"},
                        List.of(tuple("v", "", OptionType.SHORT))),
                Arguments.of(
                        "short with value",
                        new String[] {"cmd", "-n=value"},
                        List.of(tuple("n", "value", OptionType.SHORT))),
                Arguments.of(
                        "bundled short flags",
                        new String[] {"cmd", "-abc"},
                        List.of(
                                tuple("a", "", OptionType.SHORT),
                                tuple("b", "", OptionType.SHORT),
                                tuple("c", "", OptionType.SHORT))),
                Arguments.of(
                        "repeated long option",
                        new String[] {"cmd", "--tag=one", "--tag=two"},
                        List.of(
                                tuple("tag", "one", OptionType.LONG),
                                tuple("tag", "two", OptionType.LONG))),
                Arguments.of(
                        "mixed long and short",
                        new String[] {"cmd", "--name=value", "-v", "-ab"},
                        List.of(
                                tuple("name", "value", OptionType.LONG),
                                tuple("v", "", OptionType.SHORT),
                                tuple("a", "", OptionType.SHORT),
                                tuple("b", "", OptionType.SHORT))),
                // The value is split on the first `=` only, so everything after it is kept. The
                // PHP reference splits on every `=` and keeps only the first segment ("a"),
                // dropping the rest; retaining the whole value is the faithful mapping.
                Arguments.of(
                        "value containing equals",
                        new String[] {"cmd", "--expr=a=b"},
                        List.of(tuple("expr", "a=b", OptionType.LONG))));
    }

    /** Spellings the factory rejects. */
    static Stream<Arguments> provideRejectedSpellings() {
        return Stream.of(
                Arguments.of(
                        "empty long option name",
                        new String[] {"cmd", "--=value"},
                        CliInteractionInvalidNonEmptyValueException.class),
                Arguments.of(
                        "bundled short with value",
                        new String[] {"cmd", "-abc=value"},
                        CliInteractionInvalidEmptyValueException.class));
    }

    private static List<String> tuple(String name, String value, OptionType type) {
        return List.of(name, value, type.name());
    }

    private static List<String> argumentValues(List<ArgumentContract> arguments) {
        return arguments.stream().map(ArgumentContract::getValue).collect(Collectors.toList());
    }

    private static List<List<String>> optionTuples(List<OptionContract> options) {
        return options.stream()
                .map(option -> tuple(option.getName(), option.getValue(), option.getType()))
                .collect(Collectors.toList());
    }

    /**
     * The first argv entry becomes the command name, and the caller falls back to the supplied
     * application name — Java's argv carries no program name to read it from.
     */
    @Test
    void testCallerAndCommandNameMapFromArgv() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"app:version"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(DEFAULT_CALLER, input.getCaller());
        assertEquals("app:version", input.getCommandName());
        assertEquals(List.of(), input.getArguments());
        assertEquals(List.of(), input.getOptions());
    }

    /** The supplied defaults stand in when argv carries no command name. */
    @Test
    void testDefaultsApplyWhenArgvIsBare() {
        InputContract empty =
                InputFactory.fromGlobals(new String[] {}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(DEFAULT_CALLER, empty.getCaller());
        assertEquals(DEFAULT_COMMAND, empty.getCommandName());
        assertEquals(List.of(), empty.getArguments());
        assertEquals(List.of(), empty.getOptions());
    }

    /**
     * Everything after the command name that is not an option becomes a positional argument, in
     * argv order.
     */
    @Test
    void testPositionalArgumentsMapInOrder() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"app:copy", "source.txt", "target.txt", "third"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        assertEquals("app:copy", input.getCommandName());
        assertEquals(
                List.of("source.txt", "target.txt", "third"), argumentValues(input.getArguments()));
    }

    /** Options and positional arguments interleave without disturbing each other's order. */
    @Test
    void testOptionsAndArgumentsInterleave() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"app:copy", "source.txt", "--force", "target.txt", "-v"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        assertEquals(List.of("source.txt", "target.txt"), argumentValues(input.getArguments()));
        assertEquals(
                List.of(tuple("force", "", OptionType.LONG), tuple("v", "", OptionType.SHORT)),
                optionTuples(input.getOptions()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOptionSpellings")
    void testOptionSpellingsMapOntoOptions(
            String name, String[] args, List<List<String>> expected) {
        InputContract input = InputFactory.fromGlobals(args, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals("cmd", input.getCommandName());
        assertEquals(expected, optionTuples(input.getOptions()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideRejectedSpellings")
    void testRejectedSpellingsThrow(
            String name, String[] args, Class<? extends Throwable> expected) {
        assertThrows(
                expected, () -> InputFactory.fromGlobals(args, DEFAULT_CALLER, DEFAULT_COMMAND));
    }

    /** hasValue() reflects whether a value was spelled out. */
    @Test
    void testOptionValuePresenceMapsFromSpelling() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "--with=value", "--without"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        List<OptionContract> options = input.getOptions();
        OptionContract with = options.get(0);
        OptionContract without = options.get(1);

        assertTrue(with.hasValue());
        assertEquals("value", with.getValue());
        assertFalse(without.hasValue());
        assertEquals("", without.getValue());
    }

    /** A repeated option is preserved once per occurrence and looked up by name. */
    @Test
    void testRepeatedOptionsAreAllRetrievable() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "--tag=one", "--tag=two", "--other=x"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        assertTrue(input.hasOption("tag"));
        assertTrue(input.hasOption("other"));
        assertFalse(input.hasOption("missing"));
        assertEquals(List.of(), input.getOption("missing"));

        List<OptionContract> tags = input.getOption("tag");

        assertEquals(2, tags.size());
        assertEquals("one", tags.get(0).getValue());
        assertEquals("two", tags.get(1).getValue());
    }

    /**
     * An option spelled in the command-name slot is parsed as an option, so the default command
     * name stands and the later bare token becomes a positional argument.
     */
    @Test
    void testOptionBeforeCommandNameLeavesTheDefaultCommandName() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"--verbose", "app:version"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(DEFAULT_COMMAND, input.getCommandName());
        assertEquals(List.of("app:version"), argumentValues(input.getArguments()));
        assertTrue(input.hasOption("verbose"));
    }

    /**
     * A space-separated option value is not attached to the option — it lands as a positional
     * argument, so only the {@code --opt=value} spelling carries a value.
     */
    @Test
    void testSpaceSeparatedOptionValueBecomesAnArgument() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "--name", "value"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(List.of("value"), argumentValues(input.getArguments()));
        assertEquals(List.of(tuple("name", "", OptionType.LONG)), optionTuples(input.getOptions()));
    }

    /**
     * A bare {@code --} ends option parsing: it is consumed, and everything after it is an operand
     * no matter how many dashes it starts with.
     */
    @Test
    void testDoubleDashEndsOptionParsing() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "--real", "--", "--not-an-option", "-x", "plain"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        assertEquals("cmd", input.getCommandName());
        assertEquals(
                List.of("--not-an-option", "-x", "plain"), argumentValues(input.getArguments()));
        assertEquals(List.of(tuple("real", "", OptionType.LONG)), optionTuples(input.getOptions()));
    }

    /** The {@code --} itself never becomes an operand, but a second one does. */
    @Test
    void testSecondDoubleDashIsAnOperand() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "--", "--", "tail"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(List.of("--", "tail"), argumentValues(input.getArguments()));
        assertEquals(List.of(), input.getOptions());
    }

    /**
     * A lone {@code -} names standard input by convention, so it is an operand rather than an
     * option — both before and after an end-of-options marker.
     */
    @Test
    void testLoneDashIsAnOperand() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"cmd", "-", "--verbose", "--", "-"},
                        DEFAULT_CALLER,
                        DEFAULT_COMMAND);

        assertEquals(List.of("-", "-"), argumentValues(input.getArguments()));
        assertEquals(
                List.of(tuple("verbose", "", OptionType.LONG)), optionTuples(input.getOptions()));
    }

    /**
     * A {@code --} spelled in the command-name slot is still consumed, so the default command name
     * stands and the following token becomes an operand.
     */
    @Test
    void testDoubleDashInTheCommandNameSlot() {
        InputContract input =
                InputFactory.fromGlobals(
                        new String[] {"--", "app:version"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals(DEFAULT_COMMAND, input.getCommandName());
        assertEquals(List.of("app:version"), argumentValues(input.getArguments()));
    }

    /** A lone {@code -} in the command-name slot fills it, since it is an operand. */
    @Test
    void testLoneDashInTheCommandNameSlot() {
        InputContract input =
                InputFactory.fromGlobals(new String[] {"-"}, DEFAULT_CALLER, DEFAULT_COMMAND);

        assertEquals("-", input.getCommandName());
        assertEquals(List.of(), input.getArguments());
    }
}
