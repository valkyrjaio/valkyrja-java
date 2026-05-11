/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message.contract;

import io.valkyrja.cli.interaction.output.contract.OutputContract;
import java.util.function.BiFunction;

public interface QuestionContract extends MessageContract {

    BiFunction<OutputContract, AnswerContract, OutputContract> getCallable();

    QuestionContract withCallable(BiFunction<OutputContract, AnswerContract, OutputContract> callable);

    AnswerContract getAnswer();

    QuestionContract withAnswer(AnswerContract answer);

    AnswerContract ask();
}
