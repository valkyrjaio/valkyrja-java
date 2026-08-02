/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.output.contract;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.writer.contract.WriterContract;
import java.util.List;

public interface OutputContract {

    List<MessageContract> getMessages();

    List<MessageContract> getWrittenMessages();

    boolean hasWrittenMessage();

    List<MessageContract> getUnwrittenMessages();

    boolean hasUnwrittenMessage();

    OutputContract withMessages(MessageContract... messages);

    OutputContract withAddedMessages(MessageContract... messages);

    OutputContract withAddedMessage(MessageContract message);

    OutputContract writeMessages();

    OutputContract writeMessage(MessageContract message);

    List<WriterContract> getWriters();

    OutputContract withWriters(WriterContract... writers);

    boolean isInteractive();

    OutputContract withIsInteractive(boolean isInteractive);

    boolean isQuiet();

    OutputContract withIsQuiet(boolean isQuiet);

    boolean isSilent();

    OutputContract withIsSilent(boolean isSilent);

    Object getExitCode();

    OutputContract withExitCode(ExitCode exitCode);

    OutputContract withExitCode(int exitCode);
}
