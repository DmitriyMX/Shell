package ru.dmitriymx.shell;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import java.util.List;

/**
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class CommandCompleter implements Completer {
    protected StringsCompleter stringsCompleter;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        ArgumentCompleter.ArgumentList parseLine = Shell.DELIMITER.delimit(buffer, cursor);
        int cursorArgumentIndex = parseLine.getCursorArgumentIndex();

        if (cursorArgumentIndex < 0) {
            return -1;
        } else {
            return stringsCompleter.complete(buffer, cursor, candidates);
        }
    }
}
