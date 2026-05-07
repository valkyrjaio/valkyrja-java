/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.command;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.util.List;
import java.util.stream.Collectors;

public class ListBashCommand {

    protected RouteContract route;
    protected RouteCollectionContract collection;
    protected OutputFactoryContract outputFactory;

    public ListBashCommand(RouteContract route, RouteCollectionContract collection, OutputFactoryContract outputFactory) {
        this.route = route;
        this.collection = collection;
        this.outputFactory = outputFactory;
    }

    public static MessageContract help() {
        return new Message("A command to list all the commands present within the Cli component for bash completion.");
    }

    public OutputContract run() {
        OutputContract output = outputFactory.createOutput();
        List<RouteContract> routes = collection.all().values().stream().collect(Collectors.toList());

        int colonAt = -1;

        if (route.hasArgument("namespace")) {
            String namespace = route.getArgument("namespace").getFirstValue();
            colonAt = namespace.indexOf(':');
            final String ns = namespace;
            routes = routes.stream()
                .filter(r -> r.getName().startsWith(ns))
                .collect(Collectors.toList());
        }

        final int finalColonAt = colonAt;
        List<String> routesForBash = routes.stream()
            .map(r -> finalColonAt >= 0 ? r.getName().substring(finalColonAt + 1) : r.getName())
            .collect(Collectors.toList());

        return output.withAddedMessages(new Message(String.join(" ", routesForBash)));
    }
}