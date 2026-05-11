/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message.contract;

import java.util.List;
import java.util.function.Predicate;

public interface AnswerContract extends MessageContract {

    String getDefaultResponse();

    AnswerContract withDefaultResponse(String defaultResponse);

    List<String> getAllowedResponses();

    AnswerContract withAllowedResponses(String... allowedResponses);

    String getUserResponse();

    AnswerContract withUserResponse(String userResponse);

    boolean hasValidationCallable();

    Predicate<String> getValidationCallable();

    AnswerContract withValidationCallable(Predicate<String> validationCallable);

    AnswerContract withoutValidationCallable();

    boolean hasBeenAnswered();

    AnswerContract withHasBeenAnswered(boolean hasBeenAnswered);

    boolean isValidResponse();
}
