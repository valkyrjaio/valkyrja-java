/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.QuestionFormatter;
import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.message.contract.QuestionContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiFunction;

public class Question extends Message implements QuestionContract {

    protected BiFunction<OutputContract, AnswerContract, OutputContract> callable;
    protected AnswerContract answer;

    public Question(
            String text,
            BiFunction<OutputContract, AnswerContract, OutputContract> callable,
            AnswerContract answer) {
        this(text, callable, answer, new QuestionFormatter());
    }

    public Question(
            String text,
            BiFunction<OutputContract, AnswerContract, OutputContract> callable,
            AnswerContract answer,
            FormatterContract formatter) {
        super(text, formatter);
        this.callable = callable;
        this.answer = answer;
    }

    protected Question copy() {
        return new Question(text, callable, answer, formatter);
    }

    @Override
    public BiFunction<OutputContract, AnswerContract, OutputContract> getCallable() {
        return callable;
    }

    @Override
    public QuestionContract withCallable(BiFunction<OutputContract, AnswerContract, OutputContract> callable) {
        Question copy = copy();
        copy.callable = callable;
        return copy;
    }

    @Override
    public AnswerContract getAnswer() {
        return answer;
    }

    @Override
    public QuestionContract withAnswer(AnswerContract answer) {
        Question copy = copy();
        copy.answer = answer;
        return copy;
    }

    @Override
    public AnswerContract ask() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                return answer;
            }
            return answer.withUserResponse(line.trim());
        } catch (IOException e) {
            return answer;
        }
    }
}