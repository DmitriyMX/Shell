package ru.dmitriymx.shell.commands;

import ru.dmitriymx.shell.Shell;

/**
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public interface Command {
    String getName();

    Shell getShell();

    void setShell(Shell shell);

    void execute(String[] args);
}
