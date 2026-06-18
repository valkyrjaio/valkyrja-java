/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.option.Option;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link Input}. */
final class InputTest {

    @Test
    void defaultConstructor() {
        var input = new Input();

        assertEquals("valkyrja", input.getCaller());
        assertEquals("list", input.getCommandName());
        assertTrue(input.getArguments().isEmpty());
        assertTrue(input.getOptions().isEmpty());
    }

    @Test
    void withCallerAndCommandName() {
        var input = new Input().withCaller("cli").withCommandName("run");

        assertEquals("cli", input.getCaller());
        assertEquals("run", input.getCommandName());
    }

    @Test
    void argumentMutations() {
        var input =
                new Input()
                        .withArguments(new Argument("a"), new Argument("b"))
                        .withAddedArgument(new Argument("c"));

        assertEquals(3, input.getArguments().size());
        assertEquals(2, input.withoutArgument("c").getArguments().size());
        assertTrue(input.withoutArguments().getArguments().isEmpty());
    }

    @Test
    void optionMutations() {
        var input =
                new Input()
                        .withOptions(new Option("v", OptionType.SHORT))
                        .withAddedOption(new Option("verbose", OptionType.LONG));

        assertEquals(2, input.getOptions().size());
        assertTrue(input.hasOption("v"));
        assertEquals(List.of(), input.getOption("missing"));
        assertEquals(1, input.getOption("verbose").size());
        assertEquals(1, input.withoutOption("v").getOptions().size());
        assertTrue(input.withoutOptions().getOptions().isEmpty());
    }

    @Test
    void hasOptionFalseWhenAbsent() {
        assertFalse(new Input().hasOption("x"));
    }
}