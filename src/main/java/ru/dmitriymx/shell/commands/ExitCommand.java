package ru.dmitriymx.shell.commands;

/**
 * Выход из Shell
 *
 * @author DmitriyMX <mail@dmitriymx.ru>
 *         2015
 */
public class ExitCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) {
        getShell().shutdown();
    }
}
