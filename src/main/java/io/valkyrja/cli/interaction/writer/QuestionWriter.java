/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.writer;

import io.valkyrja.cli.interaction.formatter.HighlightedTextFormatter;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.message.contract.QuestionContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionExpectedQuestionOutputException;
import io.valkyrja.cli.interaction.writer.contract.WriterContract;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionWriter implements WriterContract {

    @Override
    public boolean shouldWriteMessage(MessageContract message) {
        return message instanceof QuestionContract;
    }

    @Override
    public OutputContract write(OutputContract output, MessageContract message) {
        if (!(message instanceof QuestionContract question)) {
            throw new CliInteractionExpectedQuestionOutputException("This writer expects only questions");
        }
        return askQuestion(output, question);
    }

    protected OutputContract askQuestion(OutputContract output, QuestionContract question) {
        output = writeQuestion(output, question);

        AnswerContract answer = question.getAnswer();

        if (output.isInteractive() && !output.isQuiet() && !output.isSilent()) {
            answer = question.ask();

            if (!answer.isValidResponse()) {
                output = writeAnswerAfterResponse(output, answer);
                return askQuestion(output, question);
            }
        }

        output = writeAnswerAfterResponse(output, answer);

        return question.getCallable().apply(output, answer);
    }

    protected OutputContract writeQuestion(OutputContract output, QuestionContract question) {
        output = output.writeMessage(question);

        AnswerContract answer = question.getAnswer();
        List<String> validResponses = answer.getAllowedResponses();

        if (!validResponses.isEmpty()) {
            output = output.writeMessage(new Message(" ("));
            String joined = validResponses.stream()
                .map(v -> "`" + v + "`")
                .collect(Collectors.joining(" or "));
            output = output.writeMessage(new Message(joined));
            output = output.writeMessage(new Message(")"));
        }

        output = output.writeMessage(new Message(" [default: \""));
        output = output.writeMessage(new Message(answer.getDefaultResponse(), new HighlightedTextFormatter()));
        output = output.writeMessage(new Message("\"]"));

        output = output.writeMessage(new Message(":"));
        output = output.writeMessage(new NewLine());
        output = output.writeMessage(new Message("> "));

        return output;
    }

    protected OutputContract writeAnswerAfterResponse(OutputContract output, AnswerContract answer) {
        output = output.writeMessage(answer);
        output = output.writeMessage(new NewLine());
        return output;
    }
}
