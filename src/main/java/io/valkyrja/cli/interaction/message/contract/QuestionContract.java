/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.message.contract;

import io.valkyrja.cli.interaction.output.contract.OutputContract;
import java.util.function.BiFunction;

public interface QuestionContract extends MessageContract {

    BiFunction<OutputContract, AnswerContract, OutputContract> getCallable();

    QuestionContract withCallable(
            BiFunction<OutputContract, AnswerContract, OutputContract> callable);

    AnswerContract getAnswer();

    QuestionContract withAnswer(AnswerContract answer);

    AnswerContract ask();
}
