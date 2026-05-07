/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.cli.interaction.formatter.contract.FormatterContract;
import io.valkyrja.cli.interaction.message.contract.AnswerContract;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoValidationCallableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Answer extends Message implements AnswerContract {

    protected String defaultResponse;
    protected String userResponse;
    protected List<String> allowedResponses;
    protected Predicate<String> validationCallable;
    protected boolean hasBeenAnswered;

    public Answer(
            String defaultResponse,
            Predicate<String> validationCallable,
            boolean hasBeenAnswered,
            String text,
            FormatterContract formatter,
            List<String> allowedResponses) {
        super(text, formatter);

        this.allowedResponses = new ArrayList<>(allowedResponses);
        if (!this.allowedResponses.contains(defaultResponse)) {
            this.allowedResponses.add(defaultResponse);
        }
        this.validationCallable = validationCallable;
        this.defaultResponse = defaultResponse;
        this.userResponse = defaultResponse;
        this.hasBeenAnswered = hasBeenAnswered;
    }

    public Answer(String defaultResponse) {
        this(defaultResponse, null, false, "You answered: `%s`", null, new ArrayList<>());
    }

    protected Answer copy() {
        Answer copy = new Answer(defaultResponse, validationCallable, hasBeenAnswered, text, formatter, allowedResponses);
        copy.userResponse = this.userResponse;
        return copy;
    }

    @Override
    public String getText() {
        return String.format(text, userResponse);
    }

    @Override
    public String getDefaultResponse() {
        return defaultResponse;
    }

    @Override
    public AnswerContract withDefaultResponse(String defaultResponse) {
        Answer copy = copy();
        if (!copy.hasBeenAnswered) {
            copy.userResponse = defaultResponse;
        }
        copy.defaultResponse = defaultResponse;
        if (!copy.allowedResponses.contains(defaultResponse)) {
            copy.allowedResponses = new ArrayList<>(copy.allowedResponses);
            copy.allowedResponses.add(defaultResponse);
        }
        return copy;
    }

    @Override
    public List<String> getAllowedResponses() {
        return allowedResponses;
    }

    @Override
    public AnswerContract withAllowedResponses(String... allowedResponses) {
        Answer copy = copy();
        copy.allowedResponses = new ArrayList<>(Arrays.asList(allowedResponses));
        if (!copy.allowedResponses.contains(copy.defaultResponse)) {
            copy.allowedResponses.add(copy.defaultResponse);
        }
        return copy;
    }

    @Override
    public String getUserResponse() {
        return userResponse;
    }

    @Override
    public AnswerContract withUserResponse(String userResponse) {
        Answer copy = copy();
        copy.userResponse = userResponse;
        copy.hasBeenAnswered = true;
        return copy;
    }

    @Override
    public boolean hasValidationCallable() {
        return validationCallable != null;
    }

    @Override
    public Predicate<String> getValidationCallable() {
        if (validationCallable == null) {
            throw new CliInteractionNoValidationCallableException("No validation callable has been set");
        }
        return validationCallable;
    }

    @Override
    public AnswerContract withValidationCallable(Predicate<String> validationCallable) {
        Answer copy = copy();
        copy.validationCallable = validationCallable;
        return copy;
    }

    @Override
    public AnswerContract withoutValidationCallable() {
        Answer copy = copy();
        copy.validationCallable = null;
        return copy;
    }

    @Override
    public boolean hasBeenAnswered() {
        return hasBeenAnswered;
    }

    @Override
    public AnswerContract withHasBeenAnswered(boolean hasBeenAnswered) {
        Answer copy = copy();
        copy.hasBeenAnswered = hasBeenAnswered;
        return copy;
    }

    @Override
    public boolean isValidResponse() {
        Predicate<String> callable = this.validationCallable;
        String response = this.userResponse;

        return (allowedResponses.isEmpty() && callable == null)
            || allowedResponses.contains(response)
            || (callable != null && callable.test(response));
    }
}