/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.cli.command;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.TextColorFormat;
import io.valkyrja.cli.interaction.formatter.Formatter;
import io.valkyrja.cli.interaction.formatter.HighlightedTextFormatter;
import io.valkyrja.cli.interaction.message.Banner;
import io.valkyrja.cli.interaction.message.ErrorMessage;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ListCommand {

    protected RouteCollectionContract collection;
    protected OutputFactoryContract outputFactory;

    public ListCommand(RouteCollectionContract collection, OutputFactoryContract outputFactory) {
        this.collection = collection;
        this.outputFactory = outputFactory;
    }

    public static MessageContract help() {
        return new Message("A command to list all the routes present within the Http component.");
    }

    public OutputContract run() {
        OutputContract output = outputFactory.createOutput();

        Map<String, RouteContract> routes = collection.getAll(RequestMethod.ANY);

        if (routes.isEmpty()) {
            return output
                .withExitCode(ExitCode.ERROR)
                .withAddedMessages(new Banner(new ErrorMessage("No routes were found")));
        }

        List<RouteContract> sortedRoutes = routes.values().stream()
            .sorted(Comparator.comparing(RouteContract::getPath))
            .toList();

        output = output.withAddedMessages(
            new NewLine(),
            new Message("Routes:", new HighlightedTextFormatter()),
            new NewLine()
        );

        for (RouteContract route : sortedRoutes) {
            output = output.withAddedMessages(
                new Message("  "),
                new Message(route.getPath(), new Formatter(new TextColorFormat(TextColor.MAGENTA))),
                new NewLine(),
                new Message("    - "),
                new Message("Name: "),
                new Message(route.getName(), new HighlightedTextFormatter()),
                new NewLine()
            );

            output = getOutputForDynamicRoute(output, route);
        }

        return output.withAddedMessages(new NewLine());
    }

    protected OutputContract getOutputForDynamicRoute(OutputContract output, RouteContract route) {
        if (route instanceof DynamicRouteContract dynamic) {
            String regex = dynamic.getRegex();
            if (!regex.isEmpty()) {
                output = output.withAddedMessages(
                    new Message("    - "),
                    new Message("Regex: "),
                    new Message(regex, new HighlightedTextFormatter()),
                    new NewLine()
                );
            }
        }
        return output;
    }
}