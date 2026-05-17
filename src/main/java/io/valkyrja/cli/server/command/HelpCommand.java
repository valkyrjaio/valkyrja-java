/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.command;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.TextColorFormat;
import io.valkyrja.cli.interaction.formatter.Formatter;
import io.valkyrja.cli.interaction.formatter.HighlightedTextFormatter;
import io.valkyrja.cli.interaction.message.Banner;
import io.valkyrja.cli.interaction.message.ErrorMessage;
import io.valkyrja.cli.interaction.message.Header;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.Messages;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.data.option.HelpOptionParameter;
import io.valkyrja.cli.routing.data.option.NoInteractionOptionParameter;
import io.valkyrja.cli.routing.data.option.QuietOptionParameter;
import io.valkyrja.cli.routing.data.option.SilentOptionParameter;
import io.valkyrja.cli.routing.data.option.VersionOptionParameter;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand {

    protected RouteContract helpRoute;
    protected String appNamespace;
    protected String appVersion;
    protected RouteContract route;
    protected RouteCollectionContract collection;
    protected OutputFactoryContract outputFactory;

    public HelpCommand(
            String appNamespace,
            String appVersion,
            RouteContract route,
            RouteCollectionContract collection,
            OutputFactoryContract outputFactory) {
        this.appNamespace = appNamespace;
        this.appVersion = appVersion;
        this.route = route;
        this.collection = collection;
        this.outputFactory = outputFactory;
        this.helpRoute = route;
    }

    public static MessageContract help() {
        return new Message("A command to get help for a specific command.");
    }

    public OutputContract run() {
        String commandName = route.getOption("command").getFirstValue();

        if (!collection.has(commandName)) {
            return outputFactory
                    .createOutput()
                    .withExitCode(ExitCode.ERROR)
                    .withAddedMessages(
                            new Banner(
                                    new ErrorMessage(
                                            "Command `" + commandName + "` was not found.")));
        }

        helpRoute = collection.get(commandName);

        OutputContract output =
                outputFactory
                        .createOutput()
                        .withMessages(new Header(appNamespace, appVersion, route));

        return getHelpText(output);
    }

    protected OutputContract getHelpText(OutputContract output) {
        List<MessageContract> argumentMessages = getArgumentsMessages();
        List<MessageContract> optionMessages = getOptionsMessages();

        List<MessageContract> all = new ArrayList<>();
        all.add(new NewLine());
        all.add(getNameMessages());
        all.add(new NewLine());
        all.add(new NewLine());
        all.add(getDescriptionMessages());
        all.add(new NewLine());
        all.add(new NewLine());
        all.add(getUsageMessages());
        all.add(new NewLine());
        all.add(new NewLine());
        all.addAll(argumentMessages);
        all.addAll(optionMessages);

        output = output.withAddedMessages(all.toArray(new MessageContract[0]));

        if (helpRoute.hasHelpText()) {
            MessageContract helpText = helpRoute.getHelpTextMessage();
            return output.withAddedMessages(getHelpTextMessages(helpText), new NewLine());
        }

        return output;
    }

    protected List<MessageContract> getOptionsMessages() {
        List<MessageContract> optionMessages = new ArrayList<>();

        if (helpRoute.hasOptions()) {
            optionMessages.add(getOptionsHeadingMessages());
            optionMessages.add(new NewLine());
            for (OptionParameterContract option : helpRoute.getOptions()) {
                optionMessages.add(getOptionMessages(option));
            }
        }

        optionMessages.add(getGlobalOptionsHeadingMessages());
        optionMessages.add(new NewLine());
        optionMessages.add(getOptionMessages(new QuietOptionParameter()));
        optionMessages.add(getOptionMessages(new SilentOptionParameter()));
        optionMessages.add(getOptionMessages(new NoInteractionOptionParameter()));
        optionMessages.add(getOptionMessages(new HelpOptionParameter()));
        optionMessages.add(getOptionMessages(new VersionOptionParameter()));

        return optionMessages;
    }

    protected List<MessageContract> getArgumentsMessages() {
        List<MessageContract> argumentMessages = new ArrayList<>();

        if (helpRoute.hasArguments()) {
            argumentMessages.add(getArgumentsHeadingMessages());
            argumentMessages.add(new NewLine());
            for (ArgumentParameterContract argument : helpRoute.getArguments()) {
                argumentMessages.add(getArgumentMessages(argument));
            }
        }

        return argumentMessages;
    }

    protected Messages getNameMessages() {
        return new Messages(
                new Message("Name: ", new HighlightedTextFormatter()),
                new Message(helpRoute.getName()));
    }

    protected Messages getDescriptionMessages() {
        return new Messages(
                new Message("Description:", new HighlightedTextFormatter()),
                new NewLine(),
                getIndentedText(new Message(helpRoute.getDescription())));
    }

    protected Messages getHelpTextMessages(MessageContract helpText) {
        return new Messages(
                new Message("Help:", new HighlightedTextFormatter()),
                new NewLine(),
                getIndentedText(helpText),
                new NewLine());
    }

    protected Messages getUsageMessages() {
        String usage = helpRoute.getName();
        if (helpRoute.hasOptions()) {
            usage += " [options]";
        }
        usage += " [global options]";
        if (helpRoute.hasArguments()) {
            for (ArgumentParameterContract argument : helpRoute.getArguments()) {
                usage +=
                        " ["
                                + argument.getName()
                                + (argument.getValueMode() == ArgumentValueMode.ARRAY ? "..." : "")
                                + "]";
            }
        }
        return new Messages(
                new Message("Usage:", new HighlightedTextFormatter()),
                new NewLine(),
                getIndentedText(new Message(usage)));
    }

    protected Messages getOptionsHeadingMessages() {
        return new Messages(new Message("Options:", new HighlightedTextFormatter()));
    }

    protected Messages getGlobalOptionsHeadingMessages() {
        return new Messages(new Message("Global Options:", new HighlightedTextFormatter()));
    }

    protected Messages getOptionMessages(OptionParameterContract option) {
        List<MessageContract> msgs = new ArrayList<>();
        msgs.add(new Message("  "));
        msgs.add(
                new Message(
                        "--" + option.getName(),
                        new Formatter(new TextColorFormat(TextColor.MAGENTA))));

        List<String> shortNames = option.getShortNames();
        if (!shortNames.isEmpty()) {
            msgs.add(new Message(", "));
            msgs.add(
                    new Message(
                            "-" + String.join("|", shortNames),
                            new Formatter(new TextColorFormat(TextColor.MAGENTA))));
        }

        if (option.hasValueDisplayName()) {
            String valueDisplayName = option.getValueDisplayName();
            msgs.add(new Message(" "));
            String text = "";
            if (option.getValueMode() == OptionValueMode.ARRAY) {
                text = "...";
            }
            if (option.getMode() == OptionMode.REQUIRED) {
                text += "=" + valueDisplayName;
            } else {
                text += "[=" + valueDisplayName + "]";
            }
            msgs.add(new Message(text, new HighlightedTextFormatter()));
        }

        msgs.add(new NewLine());
        msgs.add(new Message("    "));
        msgs.add(new Message(option.getDescription()));

        List<String> validValues = option.getValidValues();
        if (!validValues.isEmpty()) {
            String defaultValue = option.getDefaultValue();
            String valueSpacing = "\n      - ";
            msgs.add(new NewLine());
            msgs.add(new NewLine());
            msgs.add(new Message("    "));
            msgs.add(new Message("Valid values:"));
            for (String validValue : validValues) {
                msgs.add(new Message(valueSpacing + "`" + validValue + "`"));
                if (validValue.equals(defaultValue)) {
                    msgs.add(new Message(" (default)", new HighlightedTextFormatter()));
                }
            }
        }

        msgs.add(new NewLine());
        msgs.add(new NewLine());

        return new Messages(msgs.toArray(new MessageContract[0]));
    }

    protected Messages getArgumentsHeadingMessages() {
        return new Messages(new Message("Arguments:", new HighlightedTextFormatter()));
    }

    protected Messages getArgumentMessages(ArgumentParameterContract argument) {
        return new Messages(
                new Message("  "),
                new Message(argument.getName()),
                new NewLine(),
                new Message("    "),
                new Message(argument.getDescription()),
                new NewLine(),
                new NewLine());
    }

    protected MessageContract getIndentedText(MessageContract message) {
        String spaces = "  ";
        String wrappedText = wordWrap(spaces + message.getText(), 100, "\n" + spaces);
        return message.withText(wrappedText);
    }

    private static String wordWrap(String text, int width, String breakStr) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        int lineLength = 0;
        for (String word : words) {
            if (lineLength + word.length() + (lineLength > 0 ? 1 : 0) > width) {
                result.append(breakStr);
                lineLength = 0;
            } else if (lineLength > 0) {
                result.append(" ");
                lineLength++;
            }
            result.append(word);
            lineLength += word.length();
        }
        return result.toString();
    }
}
