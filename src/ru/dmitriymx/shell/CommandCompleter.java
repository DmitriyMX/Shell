package ru.dmitriymx.shell;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCompleter implements Completer {
    private ArgumentCompleter.ArgumentDelimiter delimiter = new ArgumentCompleter.WhitespaceArgumentDelimiter();
    private Map<String, IShellCommand> commandMap = new HashMap<>();
    private Completer commandNamesCompleter;

    public CommandCompleter(List<IShellCommand> commandList) {
        for (IShellCommand command : commandList) {
            commandMap.put(command.getName(), command);
        }

        String[] commandNames = new String[commandMap.size()];
        commandNames = commandMap.keySet().toArray(commandNames);
        commandNamesCompleter = new StringsCompleter(commandNames);
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