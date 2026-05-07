/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.middleware.routenotmatched;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.message.Answer;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.Question;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckCommandForTypoMiddleware implements RouteNotMatchedMiddlewareContract {

    protected RouterContract router;
    protected RouteCollectionContract collection;
    protected String defaultAnswer;
    protected RouteContract matchedRoute;

    public CheckCommandForTypoMiddleware(RouterContract router, RouteCollectionContract collection) {
        this(router, collection, "no");
    }

    public CheckCommandForTypoMiddleware(RouterContract router, RouteCollectionContract collection, String defaultAnswer) {
        this.router = router;
        this.collection = collection;
        this.defaultAnswer = defaultAnswer;
    }

    @Override
    public OutputContract routeNotMatched(InputContract input, OutputContract output, RouteNotMatchedHandlerContract handler) {
        Object routeOrOutput = checkCommandNameForTypo(input, output);

        if (routeOrOutput instanceof RouteContract matchingRoute) {
            output = router.dispatch(input.withCommandName(matchingRoute.getName()));
        } else if (routeOrOutput instanceof OutputContract o) {
            output = o;
        }

        return handler.routeNotMatched(input, output);
    }

    protected Object checkCommandNameForTypo(InputContract input, OutputContract output) {
        String name = input.getCommandName();
        List<RouteContract> commands = new ArrayList<>();

        for (RouteContract command : collection.all().values()) {
            double percent = similarText(command.getName(), name);
            if (percent >= 60) {
                commands.add(command);
            }
        }

        if (!commands.isEmpty()) {
            return askToRunSimilarCommands(output, commands);
        }

        return output;
    }

    protected Object askToRunSimilarCommands(OutputContract output, List<RouteContract> commands) {
        List<String> commandNames = commands.stream().map(RouteContract::getName).collect(Collectors.toList());

        output = output.withAddedMessages(
            new NewLine(),
            new Question(
                "Did you mean to run one of the following commands?",
                (o, answer) -> questionCallback(o, answer, commands),
                new Answer(defaultAnswer, null, false, "You answered: `%s`", null, commandNames)
            )
        ).writeMessages();

        return matchedRoute != null ? matchedRoute : output;
    }

    protected OutputContract questionCallback(OutputContract output, AnswerContract answer, List<RouteContract> commands) {
        String response = answer.getUserResponse();
        matchedRoute = !response.equals("no") ? getMatchedRoute(commands, response) : null;
        return output;
    }

    protected RouteContract getMatchedRoute(List<RouteContract> commands, String response) {
        return commands.stream()
            .filter(c -> c.getName().equals(response))
            .findFirst()
            .orElse(null);
    }

    private static double similarText(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        int common = 0;
        boolean[] usedA = new boolean[a.length()];
        boolean[] usedB = new boolean[b.length()];
        for (int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {
                if (!usedA[i] && !usedB[j] && a.charAt(i) == b.charAt(j)) {
                    usedA[i] = true;
                    usedB[j] = true;
                    common++;
                    break;
                }
            }
        }
        return (2.0 * common / (a.length() + b.length())) * 100.0;
    }
}