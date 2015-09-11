package ru.dmitriymx.shell;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import java.util.List;
import java.util.Set;

public class CommandCompleter implements Completer {
    private ArgumentCompleter.ArgumentDelimiter delimiter = new ArgumentCompleter.WhitespaceArgumentDelimiter();
    private Completer commandNamesCompleter;

    public CommandCompleter(Set<String> commandList) {
        commandNamesCompleter = new StringsCompleter(commandList);
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        ArgumentCompleter.ArgumentList parseCommandLine = parseLine(buffer, cursor);
        int cursorArgument = parseCommandLine.getCursorArgumentIndex();

        if (cursorArgument < 0) {
            return -1;
        } else {
            return commandNamesCompleter.complete(buffer, cursor, candidates);
        }
    }

    public ArgumentCompleter.ArgumentList parseLine(String buffer, int cursor) {
        return delimiter.delimit(buffer, cursor);
    }
}