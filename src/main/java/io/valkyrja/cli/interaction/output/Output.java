/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.output;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.writer.contract.WriterContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Output implements OutputContract {

    protected final List<MessageContract> writtenMessages = new ArrayList<>();
    protected final List<MessageContract> unwrittenMessages = new ArrayList<>();
    protected final List<WriterContract> writers = new ArrayList<>();
    protected boolean isInteractive;
    protected boolean isQuiet;
    protected boolean isSilent;
    protected Object exitCode;

    public Output() {
        this(true, false, false, ExitCode.SUCCESS);
    }

    public Output(
            boolean isInteractive,
            boolean isQuiet,
            boolean isSilent,
            ExitCode exitCode,
            MessageContract... messages) {
        this.isInteractive = isInteractive;
        this.isQuiet = isQuiet;
        this.isSilent = isSilent;
        this.exitCode = exitCode;
        this.unwrittenMessages.addAll(Arrays.asList(messages));
    }

    @Override
    public List<MessageContract> getMessages() {
        List<MessageContract> all = new ArrayList<>(writtenMessages);
        all.addAll(unwrittenMessages);
        return Collections.unmodifiableList(all);
    }

    @Override
    public List<MessageContract> getWrittenMessages() {
        return Collections.unmodifiableList(writtenMessages);
    }

    @Override
    public boolean hasWrittenMessage() {
        return !writtenMessages.isEmpty();
    }

    @Override
    public List<MessageContract> getUnwrittenMessages() {
        return Collections.unmodifiableList(unwrittenMessages);
    }

    @Override
    public boolean hasUnwrittenMessage() {
        return !unwrittenMessages.isEmpty();
    }

    @Override
    public OutputContract withMessages(MessageContract... messages) {
        Output copy = copy();
        copy.unwrittenMessages.clear();
        copy.unwrittenMessages.addAll(Arrays.asList(messages));
        return copy;
    }

    @Override
    public OutputContract withAddedMessages(MessageContract... messages) {
        Output copy = copy();
        copy.unwrittenMessages.addAll(Arrays.asList(messages));
        return copy;
    }

    @Override
    public OutputContract withAddedMessage(MessageContract message) {
        Output copy = copy();
        copy.unwrittenMessages.add(message);
        return copy;
    }

    @Override
    public OutputContract writeMessages() {
        Output copy = copy();
        List<MessageContract> pending = new ArrayList<>(copy.unwrittenMessages);
        copy.unwrittenMessages.clear();
        for (MessageContract message : pending) {
            copy.writeMessageInternal(message);
        }
        return copy;
    }

    @Override
    public OutputContract writeMessage(MessageContract message) {
        Output copy = copy();
        copy.writeMessageInternal(message);
        return copy;
    }

    @Override
    public List<WriterContract> getWriters() {
        return Collections.unmodifiableList(writers);
    }

    @Override
    public OutputContract withWriters(WriterContract... writers) {
        Output copy = copy();
        copy.writers.clear();
        copy.writers.addAll(Arrays.asList(writers));
        return copy;
    }

    @Override
    public boolean isInteractive() {
        return isInteractive;
    }

    @Override
    public OutputContract withIsInteractive(boolean isInteractive) {
        Output copy = copy();
        copy.isInteractive = isInteractive;
        return copy;
    }

    @Override
    public boolean isQuiet() {
        return isQuiet;
    }

    @Override
    public OutputContract withIsQuiet(boolean isQuiet) {
        Output copy = copy();
        copy.isQuiet = isQuiet;
        return copy;
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    @Override
    public OutputContract withIsSilent(boolean isSilent) {
        Output copy = copy();
        copy.isSilent = isSilent;
        return copy;
    }

    @Override
    public Object getExitCode() {
        return exitCode;
    }

    @Override
    public OutputContract withExitCode(ExitCode exitCode) {
        Output copy = copy();
        copy.exitCode = exitCode;
        return copy;
    }

    @Override
    public OutputContract withExitCode(int exitCode) {
        Output copy = copy();
        copy.exitCode = exitCode;
        return copy;
    }

    protected void writeMessageInternal(MessageContract message) {
        writtenMessages.add(message);
        if (!isSilent && !(isQuiet && ExitCode.SUCCESS.equals(exitCode))) {
            outputMessage(message);
        }
    }

    protected void outputMessage(MessageContract message) {
        System.out.print(message.getFormattedText());
    }

    protected Output copy() {
        Output copy = newInstance();
        copy.writtenMessages.addAll(this.writtenMessages);
        copy.unwrittenMessages.addAll(this.unwrittenMessages);
        copy.writers.addAll(this.writers);
        copy.isInteractive = this.isInteractive;
        copy.isQuiet = this.isQuiet;
        copy.isSilent = this.isSilent;
        copy.exitCode = this.exitCode;
        return copy;
    }

    protected Output newInstance() {
        return new Output();
    }
}
