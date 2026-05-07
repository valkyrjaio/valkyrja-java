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
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand {

    protected String appNamespace;
    protected String appVersion;
    protected RouteContract route;
    protected RouteCollectionContract collection;
    protected OutputFactoryContract outputFactory;

    public ListCommand(
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
    }

    public static MessageContract help() {
        return new Message("A command to list all the commands present within the Cli component.");
    }

    public OutputContract run() {
        String namespace = "";
        List<RouteContract> routes = collection.all().values().stream().collect(Collectors.toList());

        if (route.hasOption("namespace")) {
            namespace = route.getOption("namespace").getFirstValue();
            final String ns = namespace;
            routes = routes.stream()
                .filter(r -> r.getName().startsWith(ns))
                .collect(Collectors.toList());
        }

        if (routes.isEmpty()) {
            return getNoRoutesErrorOutput(namespace);
        }

        routes = routes.stream()
            .sorted(Comparator.comparing(RouteContract::getName))
            .collect(Collectors.toList());

        OutputContract output = outputFactory.createOutput()
            .withMessages(new Header(appNamespace, appVersion, route));

        output = addHeaderMessages(output, namespace);
        output = addRoutesMessages(output, routes);

        return output.withAddedMessages(new NewLine());
    }

    protected OutputContract getNoRoutesErrorOutput(String namespace) {
        String errorMessage = namespace.isEmpty()
            ? "No routes found."
            : "Namespace `" + namespace + "` was not found.";
        return outputFactory.createOutput()
            .withExitCode(ExitCode.ERROR)
            .withAddedMessages(new Banner(new ErrorMessage(errorMessage)));
    }

    protected OutputContract addHeaderMessages(OutputContract output, String namespace) {
        return output.withAddedMessages(
            new Message("Commands" + (namespace.isEmpty() ? ":" : " [" + namespace + "]:"), new HighlightedTextFormatter()),
            new NewLine()
        );
    }

    protected OutputContract addRoutesMessages(OutputContract output, List<RouteContract> routes) {
        for (RouteContract r : routes) {
            output = addRouteMessages(output, r);
        }
        return output;
    }

    protected OutputContract addRouteMessages(OutputContract output, RouteContract route) {
        return output.withAddedMessages(
            new Message("  "),
            new Message(route.getName(), new Formatter(new TextColorFormat(TextColor.MAGENTA))),
            new NewLine(),
            new Message("    - "),
            new Message(route.getDescription(), new HighlightedTextFormatter()),
            new NewLine()
        );
    }
}