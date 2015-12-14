package ru.dmitriymx.shell.commands;

import ru.dmitriymx.shell.Shell;

/**
 * Шаблон для пользовательских команд
 *
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public abstract class AbstractCommand implements Command {
    private Shell shell;

    @Override
    public Shell getShell() {
        return shell;
    }

    @Override
    public void setShell(Shell shell) {
        this.shell = shell;
    }
}
